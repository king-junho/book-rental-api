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
    @Autowired private BookService bookService;
    @Autowired private BookDaoJdbc bookDao;
    private Book book1,book2,book3,book4,book5,book6,book7,book8,book9,book10;

    public void setBookService(BookService bookService) {
        this.bookService = bookService;
    }

    @BeforeEach
    public void setUp(){
        bookService.deleteAll();

        book1 = new Book("9791158392239", "TOPCIT 소프트웨어 개발 에센스", "정보통신기획평가원", "IT/컴퓨터", 1);
        book2 = new Book("9788966260959", "파이썬 웹 스크래핑 완벽 가이드", "라이언 미첼", "IT/컴퓨터", 3);
        book3 = new Book("9791162245552", "스위프트로 배우는 iOS 앱 개발", "야곰", "IT/컴퓨터", 4);
        book4 = new Book("9788998078335", "나의 로망, 파리 여행", "김파리", "여행", 2);
        book5 = new Book("9788954699075", "클린 아키텍처", "로버트 C. 마틴", "IT/컴퓨터", 10);

        book6 = new Book("9788966260966", "클린 코드", "로버트 C. 마틴", "IT/컴퓨터", 5);
        book7 = new Book("9791162245569", "스위프트 프로그래밍", "야곰", "IT/컴퓨터", 7);
        book8 = new Book("9788998078336", "리얼 파리 여행 가이드", "김파리", "여행", 3);
        book9 = new Book("9781111111111", "클린 아키텍처", "송마틴", "IT/컴퓨터", 2);
        book10 = new Book("9782222222222", "나의 로망, 파리 여행", "이런던", "여행", 1);

        bookService.addBook(book1);
        bookService.addBook(book2);
        bookService.addBook(book3);
        bookService.addBook(book4);
        bookService.addBook(book5);

        bookService.addBook(book6);
        bookService.addBook(book7);
        bookService.addBook(book8);
        bookService.addBook(book9);
        bookService.addBook(book10);
    }

    @Test
    public void addAndDeleteBook(){
        bookService.deleteAll();
        bookService.addBook(book1);
        assertThat(bookDao.getCount()).isEqualTo(1);
    }

    @Test
    public void findBookByISBN(){
        Book findBook=bookService.findBookByISBN(book1.getISBN());
        assertThat(findBook).isNotNull();

        assertThat(findBook.getISBN()).isEqualTo(book1.getISBN());
        assertThat(findBook.getTitle()).isEqualTo(book1.getTitle());
        assertThat(findBook.getAuthor()).isEqualTo(book1.getAuthor());
        assertThat(findBook.getTotalQuantity()).isEqualTo(book1.getTotalQuantity());
    }

    @Test
    public void deleteBookByISBN(){
        bookService.deleteAll();

        bookService.addBook(book1);
        bookService.addBook(book2);
        bookService.addBook(book3);

        assertThat(bookDao.getCount()).isEqualTo(3);

        bookService.removeBookByISBN(book1.getISBN());
        assertThat(bookDao.getCount()).isEqualTo(2);
    }

    @Test
    public void rentBook(){
        Book rentBook = bookService.findBookByISBN(book1.getISBN());
        assertThat(rentBook.getTotalQuantity()).isEqualTo(1);
        assertThat(rentBook.getAvailableQuantity()).isEqualTo(1);

        bookService.rentBookByISBN(rentBook.getISBN());
        rentBook = bookService.findBookByISBN(book1.getISBN());
        assertThat(rentBook.getTotalQuantity()).isEqualTo(1);
        assertThat(rentBook.getAvailableQuantity()).isEqualTo(0);

        assertThatThrownBy(()->bookService.rentBookByISBN(book1.getISBN())).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void findByKeyword(){
        //title 검색
        List<Book> findBooks = bookService.findBookByTitle("클린 아키텍처");
        assertThat(findBooks.size()).isEqualTo(2);
        assertThat(findBooks)
                .extracting(Book::getISBN)
                .contains(book5.getISBN(),book9.getISBN());

        //author 검색
        findBooks = bookService.findBookByAuthor("로버트 C. 마틴");
        assertThat(findBooks.size()).isEqualTo(2);
        assertThat(findBooks)
                .extracting(Book::getISBN)
                .contains(book5.getISBN(),book6.getISBN());

        //genre 검색
        findBooks = bookService.findBookByGenre("여행");
        assertThat(findBooks.size()).isEqualTo(3);
        assertThat(findBooks)
                .extracting(Book::getISBN)
                .contains(book4.getISBN(),book8.getISBN(),book10.getISBN());
    }
}
