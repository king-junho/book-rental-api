package dao;

import domain.Book;
import domain.BookItem;
import exception.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("/bookDaoTestContext.xml")
public class BookItemDaoTest {
    @Autowired
    private TestDataCleaner testDataCleaner;
    @Autowired
    private BookItemDao bookItemDao;
    @Autowired
    private BookDao bookDao;

    @BeforeEach
    public void setUp(){
        testDataCleaner.cleanUp();
    }

    private void addBookData(String isbn){
        Book book = new Book(isbn,"testTitle","testAuthor","testDescription");
        bookDao.add(book);
        bookItemDao.add(book.getIsbn());
    }

    @Test
    public void add_NewBookItem_IncreaseAvailableCount(){
        String isbn = "isbn";
        addBookData(isbn);
        assertThat(bookItemDao.getAvailableBookCount(isbn)).isEqualTo(1);
    }

    @Test
    public void rent_AvailableBook_ReturnOne(){
        String isbn = "isbn";
        addBookData(isbn);
        BookItem bookItem = bookItemDao.findByIsbn(isbn).get(0);
        assertThat(bookItemDao.getAvailableBookCount(isbn)).isEqualTo(1);
        int updateRows = bookItemDao.rent(bookItem.getId());

        assertThat(updateRows).isEqualTo(1);
        assertThat(bookItemDao.getAvailableBookCount(isbn)).isEqualTo(0);
    }

    @Test
    public void returnBook_RentedBookItem_ReturnsOne(){
        String isbn = "isbn";
        addBookData(isbn);
        BookItem bookItem = bookItemDao.findByIsbn(isbn).get(0);
        assertThat(bookItemDao.getAvailableBookCount(isbn)).isEqualTo(1);

        int updateRows = bookItemDao.rent(bookItem.getId());
        assertThat(updateRows).isEqualTo(1);
        assertThat(bookItemDao.getAvailableBookCount(isbn)).isEqualTo(0);

        updateRows = bookItemDao.returnBook(bookItem.getId());
        assertThat(updateRows).isEqualTo(1);
        assertThat(bookItemDao.getAvailableBookCount(isbn)).isEqualTo(1);
    }

    @Test
    public void findById_NonExistentId_ThrowsEntityNotFoundException(){
        Long invalidId = 9999L;

        assertThatThrownBy(() -> bookItemDao.findById(invalidId)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    public void findByIsbn_ValidIsbn_ReturnsCorrectBookItem(){
        String isbn = "isbn";
        addBookData(isbn);
        BookItem bookItem = bookItemDao.findAvailableBooks(isbn).get(0);
        BookItem foundBookItem = bookItemDao.findByIsbn(bookItem.getIsbn()).get(0);

        assertThat(bookItem.getId()).isEqualTo(foundBookItem.getId());
    }

    @Test
    public void addBatch_MultipleBookItems_IncreasesAvailableCount(){
        String isbn = "isbn";
        addBookData(isbn);
        bookItemDao.addBatch(isbn,10);

        assertThat(bookItemDao.getAvailableBookCount(isbn)).isEqualTo(11);
    }

    @Test
    public void rent_RentedBookItem_ReturnsZero(){
        String isbn = "isbn";
        addBookData(isbn);
        BookItem bookItem = bookItemDao.findByIsbn(isbn).get(0);

        assertThat(bookItemDao.rent(bookItem.getId())).isEqualTo(1);

        assertThat(bookItemDao.rent(bookItem.getId())).isEqualTo(0);
    }

    @Test
    public void returnBook_AvailableBookItem_ReturnsZero(){
        String isbn = "isbn";
        addBookData(isbn);
        BookItem bookItem = bookItemDao.findByIsbn(isbn).get(0);

        assertThat(bookItemDao.returnBook(bookItem.getId())).isEqualTo(0);
    }

    @Test
    public void findById_ValidId_ReturnsCorrectBookItem(){
        String isbn = "isbn";
        addBookData(isbn);

        BookItem bookItem = bookItemDao.findByIsbn(isbn).get(0);
        BookItem findBookItem = bookItemDao.findById(bookItem.getId());

        assertThat(bookItem.getId()).isEqualTo(findBookItem.getId());
        assertThat(bookItem.getIsbn()).isEqualTo(findBookItem.getIsbn());
        assertThat(bookItem.getStatus()).isEqualTo(findBookItem.getStatus());
    }

    @Test
    public void findAvailableBooks_MixedStatuses_ReturnsOnlyAvailableBookItems(){
        String isbn = "isbn";
        addBookData(isbn);

        bookItemDao.addBatch(isbn,9);

        List<BookItem> bookItems = bookItemDao.findAvailableBooks(isbn);

        bookItemDao.rent(bookItems.get(0).getId());
        bookItemDao.rent(bookItems.get(1).getId());
        bookItemDao.rent(bookItems.get(2).getId());

        List<BookItem> availableBookItems = bookItemDao.findAvailableBooks(isbn);
        assertThat(availableBookItems.size()).isEqualTo(7);
        Assertions.assertThat(availableBookItems)
                .extracting(BookItem::getId)
                .containsExactly(
                        bookItems.get(3).getId(),
                        bookItems.get(4).getId(),
                        bookItems.get(5).getId(),
                        bookItems.get(6).getId(),
                        bookItems.get(7).getId(),
                        bookItems.get(8).getId(),
                        bookItems.get(9).getId()
                );
    }

    @Test
    public void getAvailableBookCount_MixedStatuses_ReturnsAvailableCount(){
        String isbn = "isbn";
        addBookData(isbn);

        bookItemDao.addBatch(isbn,9);

        List<BookItem> bookItems = bookItemDao.findAvailableBooks(isbn);

        bookItemDao.rent(bookItems.get(0).getId());
        bookItemDao.rent(bookItems.get(1).getId());
        bookItemDao.rent(bookItems.get(2).getId());

        assertThat(bookItemDao.getAvailableBookCount(isbn)).isEqualTo(7);

        bookItemDao.returnBook(bookItems.get(0).getId());
        bookItemDao.returnBook(bookItems.get(1).getId());
        assertThat(bookItemDao.getAvailableBookCount(isbn)).isEqualTo(9);

    }
}
