package dao;

import domain.Book;
import domain.enums.SearchType;
import exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("/bookDaoTestContext.xml")
public class BookDaoTest {
    @Autowired private BookDao bookDao;
    @Autowired private TestDataCleaner testDataCleaner;

    @BeforeEach
    public void setUp(){
        testDataCleaner.cleanUp();
    }

    @Test
    public void addAndDelete_Book(){
        Book book = new Book("isbn","title","author","genre");
        bookDao.add(book);

        assertThat(bookDao.getCountBySearchType(SearchType.TITLE,"title")).isEqualTo(1);
        bookDao.deleteByIsbn("isbn");
        assertThat(bookDao.getCountBySearchType(SearchType.TITLE,"title")).isEqualTo(0);
    }

    @Test
    public void findBook_ByIsbn(){
        Book book = new Book("isbn","title","author","genre");
        bookDao.add(book);

        Book findBook = bookDao.findByIsbn(book.getIsbn());

        assertThat(findBook).isNotNull();
        assertThat(findBook.getIsbn()).isEqualTo(book.getIsbn());
        assertThat(findBook.getTitle()).isEqualTo(book.getTitle());
        assertThat(findBook.getAuthor()).isEqualTo(book.getAuthor());
        assertThat(findBook.getGenre()).isEqualTo(book.getGenre());
    }


    @Test
    public void findBooks_ByTitle_WithPagination(){
        setData();

        List<Book> searchBooks = bookDao.findBySearchType(SearchType.TITLE,"title",5,0);

        assertThat(searchBooks.size()).isEqualTo(5);
        assertThat(searchBooks)
                .extracting(Book::getTitle)
                .containsExactly(
                        "title000",
                        "title001",
                        "title002",
                        "title003",
                        "title004"
                );
    }

    @Test
    public void findBooks_ByAuthor_WithPagination(){
        setData();

        List<Book> searchBooks = bookDao.findBySearchType(SearchType.AUTHOR,"author",10,0);
        assertThat(searchBooks.size()).isEqualTo(10);
        assertThat(searchBooks)
                .extracting(Book::getAuthor)
                .containsExactly(
                        "author000",
                        "author001",
                        "author002",
                        "author003",
                        "author004",
                        "author005",
                        "author006",
                        "author007",
                        "author008",
                        "author009"
                );
    }

    @Test
    public void findBooks_ByGenre_WithPagination(){
        setData();

        List<Book> searchBooks = bookDao.findBySearchType(SearchType.GENRE,"genre",10,5);
        assertThat(searchBooks.size()).isEqualTo(10);
        assertThat(searchBooks)
                .extracting(Book::getGenre)
                .containsExactly(
                        "genre005",
                        "genre006",
                        "genre007",
                        "genre008",
                        "genre009",
                        "genre010",
                        "genre011",
                        "genre012",
                        "genre013",
                        "genre014"
                );
    }

    @Test
    public void findBook_ByIsbn_ThrowsException_WhenBookDoesNotExist(){
        assertThatThrownBy(()->bookDao.findByIsbn("unknownIsbn")).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    public void exists_ByIsbn_ReturnsTrue_WhenBookExist(){
        setData();
        assertThat(bookDao.existsByIsbn("isbn000")).isTrue();
    }

    @Test
    public void exists_ByIsbn_ReturnsFalse_WhenBookDoesNotExist(){
        setData();
        assertThat(bookDao.existsByIsbn("unknown")).isFalse();
    }

    @Test
    public void findBooks_ReturnsEmptyList_WhenBookDoesNotExist(){
        setData();
        List<Book> books = bookDao.findBySearchType(SearchType.TITLE,"unknown",5,0);
        assertThat(books).isEmpty();
    }

    private void setData(){
        for(int i=0; i<20; i++){
            Book book = new Book("isbn"+String.format("%03d",i),"title"+String.format("%03d",i),"author"+String.format("%03d",i),"genre"+String.format("%03d",i));
            bookDao.add(book);
        }
    }
}
