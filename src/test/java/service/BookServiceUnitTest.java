package service;

import dao.BookDao;
import domain.Book;
import domain.Page;
import domain.PageResult;
import domain.enums.SearchType;
import exception.EntityAlreadyExistsException;
import exception.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
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
    public void findBookBySearchType_ValidCondition_ReturnPageResult(){
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

        when(bookDao.getCountBySearchType(type,title)).thenReturn(searchCount);

        when(bookDao.findBySearchType(type,title,expectedPage.getPageSize(),expectedPage.getStartIndex())).thenReturn(List.of(book));

        //when
        PageResult<Book> result = bookService.findBookBySearchType(type,title,currentPage);

        //then
        assertThat(result.getData().size()).isEqualTo(searchCount);
        assertThat(result.getData().contains(book)).isTrue();


        verify(bookDao).getCountBySearchType(type,title);
        verify(bookDao).findBySearchType(type,title,expectedPage.getPageSize(),expectedPage.getStartIndex());

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
}
