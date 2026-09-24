package service;

import dao.BookDao;
import domain.Book;
import service.model.BookDetail;
import service.model.BookSearchResult;
import service.model.Page;
import domain.enums.SearchType;
import exception.EntityAlreadyExistsException;
import exception.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceUnitTest {
    @Mock private BookDao bookDao;
    @Mock private BookItemService bookItemService;
    @InjectMocks private BookService bookService;

    @Test
    public void findBookById_ExistingBook_ReturnBook(){
        //given
        String isbn="isbn";
        String title="title";
        String author="author";
        String genre="genre";
        Book book = new Book(isbn,title,author,genre);
        when(bookDao.findByIsbn(isbn)).thenReturn(book);

        //when
        Book findBook = bookService.findBookById(isbn);

        //then
        assertThat(findBook).isSameAs(book);
    }

    @Test
    public void findBookById_NonExistentBook_ThrowEntityNotFoundException(){
        //given
        String isbn="isbn";

        when(bookDao.findByIsbn(isbn)).thenThrow(EntityNotFoundException.class);

        //when && then
        assertThatThrownBy(()->bookService.findBookById(isbn)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    public void findBookBySearchType_ValidCondition_ReturnSearchResult(){
        //given
        int searchCount = 1;
        int currentPage = 1;
        Page expectedPage = new Page(searchCount,currentPage);

        String isbn="isbn";
        String title="title";
        String author="author";
        String genre="genre";

        Book book = new Book(isbn,title,author,genre);
        SearchType type = SearchType.TITLE;

        int totalCount = 5;
        int availableCount = 3;

        when(bookDao.getCountBySearchType(type,title)).thenReturn(searchCount);

        when(bookDao.findBySearchType(type,title,expectedPage.getPageSize(),expectedPage.getStartIndex())).thenReturn(List.of(book));

        when(bookItemService.getBookCount(isbn)).thenReturn(totalCount);

        when(bookItemService.getAvailableBookCount(isbn)).thenReturn(availableCount);

        //when
        BookSearchResult result = bookService.findBookBySearchType(type,title,currentPage);

        //then
        assertThat(result.books()).hasSize(1);

        BookDetail detail = result.books().get(0);

        assertThat(detail.book()).isSameAs(book);
        assertThat(detail.totalCount()).isEqualTo(totalCount);
        assertThat(detail.availableCount()).isEqualTo(availableCount);
        assertThat(result.page().getCurrentPage()).isEqualTo(currentPage);
        assertThat(result.page().getListCount()).isEqualTo(searchCount);

        verify(bookDao).getCountBySearchType(type, title);
        verify(bookDao).findBySearchType(type, title, expectedPage.getPageSize(), expectedPage.getStartIndex());
        verify(bookItemService).getBookCount(isbn);
        verify(bookItemService).getAvailableBookCount(isbn);

    }

    @Test
    public void removeBookById_ExistingBook_DeleteSuccess(){
        //given
        String isbn="isbn";

        when(bookDao.existsByIsbn(isbn)).thenReturn(true);

        //then
        bookService.removeBookById(isbn);

        verify(bookDao).existsByIsbn(isbn);
        verify(bookDao).deleteByIsbn(isbn);
    }

    @Test
    public void removeBookById_NonExistentBook_ThrowEntityNotFoundException(){
        //given
        String isbn="isbn";

        when(bookDao.existsByIsbn(isbn)).thenReturn(false);

        //then
        assertThatThrownBy(()->bookService.removeBookById(isbn)).isInstanceOf(EntityNotFoundException.class);

        verify(bookDao).existsByIsbn(isbn);
        verify(bookDao,never()).deleteByIsbn(isbn);
    }

    @Test
    public void addBook_NewBook_SaveBookAndBookItems(){
        //given
        String isbn="isbn";
        String title="title";
        String author="author";
        String genre="genre";
        Book book = new Book(isbn,title,author,genre);

        when(bookDao.existsByIsbn(isbn)).thenReturn(false);


        //when
        bookService.addBook(book,1);

        //then
        verify(bookDao).add(book);
        verify(bookItemService).addBookItem(book.getIsbn(),1);
    }

    @Test
    public void addBook_DuplicateIsbn_ThrowEntityAlreadyExistsException(){
        //given
        String isbn="isbn";
        String title="title";
        String author="author";
        String genre="genre";
        Book book = new Book(isbn,title,author,genre);

        when(bookDao.existsByIsbn(isbn)).thenReturn(true);

        //when && then
        assertThatThrownBy(()->bookService.addBook(book,1)).isInstanceOf(EntityAlreadyExistsException.class);

        verify(bookDao,never()).add(book);
        verify(bookItemService,never()).addBookItem(book.getIsbn(),1);


    }

    @Test
    public void addBook_NegativeCount_ThrowIllegalArgumentException(){
        //given
        String isbn="isbn";
        String title="title";
        String author="author";
        String genre="genre";
        Book book = new Book(isbn,title,author,genre);
        int count=-1;

        //when && then
        assertThatThrownBy(()->bookService.addBook(book,count)).isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(bookDao, bookItemService);
    }
    @Test
    public void findBookDetail_ExistingBook_ReturnBookDetail(){
        // given
        String isbn = "isbn";
        String title = "title";
        String author = "author";
        String genre = "genre";

        Book book = new Book(isbn, title, author, genre);

        int totalCount = 5;
        int availableCount = 3;

        when(bookDao.findByIsbn(isbn)).thenReturn(book);
        when(bookItemService.getBookCount(isbn)).thenReturn(totalCount);
        when(bookItemService.getAvailableBookCount(isbn))
                .thenReturn(availableCount);

        //when
        BookDetail result = bookService.findBookDetail(isbn);

        //then
        assertThat(result.book()).isEqualTo(book);
        assertThat(result.totalCount()).isEqualTo(totalCount);
        assertThat(result.availableCount()).isEqualTo(availableCount);

        verify(bookDao).findByIsbn(isbn);
        verify(bookItemService).getBookCount(isbn);
        verify(bookItemService).getAvailableBookCount(isbn);
    }
    @Test
    public void findBookDetail_NonExistentBook_ThrowEntityNotFoundException(){
        //given
        when(bookDao.findByIsbn("null")).thenThrow(EntityNotFoundException.class);

        //when && then
        assertThatThrownBy(()->bookService.findBookDetail("null")).isInstanceOf(EntityNotFoundException.class);

        verifyNoInteractions(bookItemService);
    }
}
