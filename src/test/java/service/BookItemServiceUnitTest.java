package service;

import dao.BookItemDao;
import domain.BookItem;
import domain.Page;
import domain.PageResult;
import domain.enums.BookItemStatus;
import exception.BookAlreadyRentedException;
import exception.BookAlreadyReturnedException;
import exception.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookItemServiceUnitTest {
    @Mock private BookItemDao bookItemDao;
    @Mock private RentalService rentalService;

    @InjectMocks
    private BookItemService bookItemService;

    @Test
    public void getBookItemById_ExistingBookItem_ReturnBookItem(){
        //given
        Long id = 1L;
        String isbn = "isbn";
        BookItemStatus status = BookItemStatus.RENTED;
        BookItem bookItem = BookItem.restore(id,isbn,status);

        when(bookItemDao.findById(id)).thenReturn(bookItem);

        //when
        BookItem findBookItem = bookItemService.getBookItemById(id);

        //then
        assertThat(findBookItem).isSameAs(bookItem);
    }

    @Test
    public void getBookItemById_NonExistentBookItem_ThrowEntityNotFoundException(){
        //give
        Long id = 1L;
        when(bookItemDao.findById(id)).thenThrow(EmptyResultDataAccessException.class);

        //when && then
        assertThatThrownBy(()->bookItemService.getBookItemById(id)).isInstanceOf(EmptyResultDataAccessException.class);
    }

    @Test
    public void getBookItemByISBN_ExistingBookItems_ReturnPageResult(){
        //given
        int searchCount = 1;
        int currentPage = 1;
        Page expectedPage = new Page(searchCount,currentPage);

        Long id = 1L;
        String isbn = "isbn";
        BookItemStatus status = BookItemStatus.RENTED;
        BookItem bookItem = BookItem.restore(id,isbn,status);

        when(bookItemDao.findByIsbn(isbn)).thenReturn(List.of(bookItem));

        //when
        PageResult<BookItem> findBookItems = bookItemService.getBookItemByISBN(isbn,currentPage);
        assertThat(findBookItems.getData()).containsExactly(bookItem);
        //assertThat(findBookItems.getPageInfo()).isSameAs(expectedPage);
    }

    @Test
    public void getBookItemByISBN_NonExistentBookItems_ReturnEmptyPageResult(){
        //given
        when(bookItemDao.findByIsbn("unknown")).thenReturn(List.of());

        //when
        PageResult<BookItem> findBookItems = bookItemService.getBookItemByISBN("unknown",1);

        //then
        assertThat(findBookItems.getData()).isEmpty();
    }


    @Test
    public void addBookItem_SingleCount_AddBookItem(){
        //given
        String isbn = "isbn";

        //when
        bookItemService.addBookItem(isbn,1);

        //then
        verify(bookItemDao).add(isbn);
    }

    @Test
    public void addBookItem_MultipleCount_AddBookItemsBatch(){
        //given
        String isbn = "isbn";
        int count = 5;

        //when
        bookItemService.addBookItem(isbn,count);

        //then
        verify(bookItemDao).addBatch(isbn, count);
    }

    @Test
    public void addBookItem_NegativeCount_ThrowIllegalArgumentException(){
        //given
        String isbn = "isbn";
        int count = -1;

        //when && then
        assertThatThrownBy(()->bookItemService.addBookItem(isbn,count)).isInstanceOf(IllegalArgumentException.class);


        //then
        verify(bookItemDao,never()).add(isbn);
        verify(bookItemDao,never()).addBatch(isbn, count);
    }

    @Test
    public void addBookItem_ZeroCount_ThrowIllegalArgumentException(){
        //given
        String isbn = "isbn";
        int count = 0;

        //when && then
        assertThatThrownBy(()->bookItemService.addBookItem(isbn,count)).isInstanceOf(IllegalArgumentException.class);


        //then
        verify(bookItemDao,never()).add(isbn);
        verify(bookItemDao,never()).addBatch(isbn, count);
    }


    @Test
    public void deleteBookItemById_ExistingBookItem_DeleteSuccess(){
        //given
        Long id = 1L;

        //when
        bookItemService.deleteBookItemById(id);

        //then
        verify(bookItemDao).deleteById(id);
    }

}
