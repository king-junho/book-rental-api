package service;

import dao.BookItemDao;
import domain.BookItem;
import exception.EntityNotFoundException;
import service.model.Page;
import domain.enums.BookItemStatus;
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
public class BookItemServiceUnitTest {
    @Mock private BookItemDao bookItemDao;

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
        when(bookItemDao.findById(id)).thenThrow(EntityNotFoundException.class);

        //when && then
        assertThatThrownBy(()->bookItemService.getBookItemById(id)).isInstanceOf(EntityNotFoundException.class);
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

    @Test
    public void getBookCount_ExistingBook_ReturnTotalCount(){
        // given
        String isbn = "isbn";

        when(bookItemDao.getBookCount(isbn)).thenReturn(5);

        // when
        int count = bookItemService.getBookCount(isbn);

        // then
        assertThat(count).isEqualTo(5);
        verify(bookItemDao).getBookCount(isbn);
    }

    @Test
    public void getAvailableBookCount_ExistingBook_ReturnAvailableCount(){
        // given
        String isbn = "isbn";

        when(bookItemDao.getAvailableBookCount(isbn)).thenReturn(3);

        // when
        int count = bookItemService.getAvailableBookCount(isbn);

        // then
        assertThat(count).isEqualTo(3);
        verify(bookItemDao).getAvailableBookCount(isbn);
    }

}
