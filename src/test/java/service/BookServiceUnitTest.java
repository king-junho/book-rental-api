package service;

import dao.BookDao;
import dao.BookItemDao;
import domain.Book;
import domain.PageResult;
import domain.enums.SearchType;
import exception.BookAlreadyRentedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceUnitTest {
    @Mock private BookDao bookDao;
    @Mock private BookItemDao bookItemDao;
    @InjectMocks private BookService bookService;

    @Test
    public void findBySearchBook(){
        when(bookDao.getCountBySearchType(SearchType.TITLE,"클린")).thenReturn(1);

        Book mockBook = new Book("123", "클린 코드", "마틴", "IT");
        when(bookDao.findBySearchType(eq(SearchType.TITLE),eq("클린"),anyInt(),anyInt()))
                .thenReturn(List.of(mockBook));

        PageResult<Book> result = bookService.findBookBySearchType(SearchType.TITLE,"클린",1);

        assertThat(result.getData().size()).isEqualTo(1);
        assertThat(result.getData().get(0).getTitle()).isEqualTo("클린 코드");
    }

//    @Test
//    public void accessRentedBook(){
//        Long targetBookItemId = 1L;
//        when(bookItemDao.rent(targetBookItemId)).thenReturn(0);
//
//        assertThatThrownBy(()->bookService.rentBookItem(targetBookItemId))
//                .isInstanceOf(BookAlreadyRentedException.class)
//                .hasMessage("이미 대여 중이거나 존재하지 않는 도서입니다.");
//    }

}
