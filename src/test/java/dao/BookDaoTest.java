package dao;

import domain.Book;
import domain.enums.SearchType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("/bookDaoTestContext.xml")
public class BookDaoTest {
    @Autowired private BookDao bookDao;

    @BeforeEach
    public void setUp(){
        bookDao.deleteAll();
    }

    @Test
    public void addAndDelete(){
        Book book = new Book("isbn","title","author","genre");
        bookDao.add(book);

        assertThat(bookDao.getCountBySearchType(SearchType.TITLE,"title")).isEqualTo(1);
        bookDao.deleteById("isbn");
        assertThat(bookDao.getCountBySearchType(SearchType.TITLE,"title")).isEqualTo(0);
    }

    @Test
    public void findBookById(){
        Book book = new Book("isbn","title","author","genre");
        bookDao.add(book);

        Book findBook = bookDao.findById(book.getISBN());

        assertThat(findBook).isNotNull();
        assertThat(findBook.getTitle()).isEqualTo(book.getTitle());
        assertThat(findBook.getAuthor()).isEqualTo(book.getAuthor());
        assertThat(findBook.getGenre()).isEqualTo(book.getGenre());
    }


    @Test
    public void findBooksByTitle(){
        setData();

        List<Book> searchBooks = bookDao.findBySearchType(SearchType.TITLE,"title",5,0);

        assertThat(searchBooks.size()).isEqualTo(5);
        assertThat(searchBooks)
                .extracting(Book::getTitle)
                .contains("title0","title1","title2","title3","title4");
    }

    @Test
    public void findBookByAuthor(){
        setData();

        List<Book> searchBooks = bookDao.findBySearchType(SearchType.AUTHOR,"author",10,0);
        assertThat(searchBooks.size()).isEqualTo(10);
        assertThat(searchBooks)
                .extracting(Book::getTitle)
                .contains("title0","title1","title2","title3","title4","title5","title6","title7","title8","title9");
    }

    @Test
    public void findBookByGenre(){
        setData();

        List<Book> searchBooks = bookDao.findBySearchType(SearchType.GENRE,"genre",10,5);
        assertThat(searchBooks.size()).isEqualTo(5);
        assertThat(searchBooks)
                .extracting(Book::getTitle)
                .contains("title5","title6","title7","title8","title9");
    }

    private void setData(){
        for(int i=0; i<10; i++){
            Book book = new Book("isbn"+i,"title"+i,"author"+i,"genre"+i);
            bookDao.add(book);
        }
    }
}
