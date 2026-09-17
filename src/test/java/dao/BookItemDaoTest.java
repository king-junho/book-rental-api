package dao;

import domain.Book;
import domain.BookItem;
import exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("/bookDaoTestContext.xml")
public class BookItemDaoTest {
    @Autowired
    private BookItemDao bookItemDao;
    @Autowired
    private BookDao bookDao;

    @BeforeEach
    public void setUp(){
        bookItemDao.deleteAll();
        bookDao.deleteAll();
    }

    private void addBookData(String isbn){
        Book book = new Book(isbn,"testTitle","testAuthor","testDescription");
        bookDao.add(book);
        bookItemDao.add(book.getISBN());
    }

    @Test
    public void add_NewBookItem_IncreaseAvailableCount(){
        String isbn = "isbn";
        addBookData(isbn);
        assertThat(bookItemDao.getAvailableBookCount(isbn)).isEqualTo(1);

        bookItemDao.addBatch(isbn,5);
        assertThat(bookItemDao.getAvailableBookCount(isbn)).isEqualTo(6);
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
    public void return_RentedBook_ReturnZero(){
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
    public void findById_NonExistentId_ThrowEntityNotFoundException(){
        Long invalidId = 9999L;

        assertThatThrownBy(() -> bookItemDao.findById(invalidId)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    public void findById_VaildId_ReturnCorrectBookItem(){
        String isbn = "isbn";
        addBookData(isbn);
        BookItem bookItem = bookItemDao.findAvailableBooks(isbn).get(0);
        BookItem foundBookItem = bookItemDao.findByIsbn(bookItem.getISBN()).get(0);

        assertThat(bookItem.getId()).isEqualTo(foundBookItem.getId());
    }
}
