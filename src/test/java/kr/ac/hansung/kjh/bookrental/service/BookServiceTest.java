package kr.ac.hansung.kjh.bookrental.service;

import kr.ac.hansung.kjh.bookrental.dto.response.BookDetailResponse;
import kr.ac.hansung.kjh.bookrental.dto.response.BookResponse;
import kr.ac.hansung.kjh.bookrental.dto.response.BookSearchResponse;
import kr.ac.hansung.kjh.bookrental.entity.BookEntity;
import kr.ac.hansung.kjh.bookrental.enums.BookItemStatus;
import kr.ac.hansung.kjh.bookrental.enums.SearchType;
import kr.ac.hansung.kjh.bookrental.exception.CustomException;
import kr.ac.hansung.kjh.bookrental.exception.ErrorCode;
import kr.ac.hansung.kjh.bookrental.repository.BookItemRepository;
import kr.ac.hansung.kjh.bookrental.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    private static final int PAGE_SIZE = 5;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookItemRepository bookItemRepository;

    @InjectMocks
    private BookService bookService;

    private static BookEntity createBook(String isbn) {
        return BookEntity.create(isbn, "자바 입문", "김준호", "프로그래밍");
    }

    @Test
    @DisplayName("도서가 존재하면 도서 정보와 전체·대여 가능 권수를 반환한다")
    void findBookById_returnsDetail_whenBookExists() {
        // given
        String isbn = "test_001";
        int totalCount = 5;
        int availableCount = 3;
        BookEntity bookEntity = createBook(isbn);

        given(bookRepository.findById(isbn)).willReturn(Optional.of(bookEntity));
        given(bookItemRepository.countByIsbn(isbn)).willReturn(totalCount);
        given(bookItemRepository.countByIsbnAndStatus(isbn, BookItemStatus.AVAILABLE)).willReturn(availableCount);

        // when
        BookDetailResponse bookDetailResponse = bookService.findBookById(isbn);

        // then
        assertThat(bookDetailResponse).isEqualTo(new BookDetailResponse("test_001", "자바 입문", "김준호", "프로그래밍", 5, 3));
    }

    @Test
    @DisplayName("도서가 없으면 BOOK_NOT_FOUND 예외를 던지고 권수를 조회하지 않는다")
    void findBookById_throwsBookNotFound_whenBookDoesNotExist() {
        // given
        String isbn = "missing_isbn";
        given(bookRepository.findById(isbn)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bookService.findBookById(isbn)).isInstanceOfSatisfying(CustomException.class,
                exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.BOOK_NOT_FOUND));

        verifyNoInteractions(bookItemRepository);
    }

    @Test
    @DisplayName("제목으로 검색하면 ISBN 오름차순의 도서 목록과 페이지 정보 반환")
    void findBookBySearchType_returnsPage_whenSearchingByTitle() {
        // given
        SearchType type = SearchType.TITLE;
        String keyword = "자바";
        int currentPage = 0;

        Pageable pageable = PageRequest.of(currentPage, PAGE_SIZE, Sort.by(Sort.Direction.ASC, "isbn"));
        BookEntity bookEntity = createBook("test_001");
        Page<BookEntity> page = new PageImpl<>(List.of(bookEntity), pageable, 1);

        given(bookRepository.findByTitleContaining(keyword, pageable)).willReturn(page);

        // when
        BookSearchResponse result = bookService.findBookBySearchType(type, keyword, currentPage);

        // then
        assertThat(result.content()).containsExactly(new BookResponse("test_001", "자바 입문", "김준호", "프로그래밍"));
        assertThat(result.page()).isZero();
        assertThat(result.size()).isEqualTo(PAGE_SIZE);
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.totalPages()).isEqualTo(1);

        verify(bookRepository).findByTitleContaining(keyword, pageable);
    }

    @Test
    @DisplayName("저자 이름으로 검색하면 ISBN 오름차순의 도서 목록과 페이지 정보 반환")
    void findBookBySearchType_returnsPage_whenSearchingByAuthor() {
        // given
        SearchType type = SearchType.AUTHOR;
        String keyword = "준호";
        int currentPage = 0;

        Pageable pageable = PageRequest.of(currentPage, PAGE_SIZE, Sort.by(Sort.Direction.ASC, "isbn"));
        BookEntity bookEntity = createBook("test_001");
        Page<BookEntity> page = new PageImpl<>(List.of(bookEntity), pageable, 1);

        given(bookRepository.findByAuthorContaining(keyword, pageable)).willReturn(page);

        // when
        BookSearchResponse result = bookService.findBookBySearchType(type, keyword, currentPage);

        // then
        assertThat(result.content()).containsExactly(new BookResponse("test_001", "자바 입문", "김준호", "프로그래밍"));
        assertThat(result.page()).isZero();
        assertThat(result.size()).isEqualTo(PAGE_SIZE);
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.totalPages()).isEqualTo(1);

        verify(bookRepository).findByAuthorContaining(keyword, pageable);
    }

    @Test
    @DisplayName("장르 이름으로 검색하면 ISBN 오름차순의 도서 목록과 페이지 정보 반환")
    void findBookBySearchType_returnsPage_whenSearchingByGenre() {
        // given
        SearchType type = SearchType.GENRE;
        String keyword = "프로그래밍";
        int currentPage = 0;

        Pageable pageable = PageRequest.of(currentPage, PAGE_SIZE, Sort.by(Sort.Direction.ASC, "isbn"));
        BookEntity bookEntity = createBook("test_001");
        Page<BookEntity> page = new PageImpl<>(List.of(bookEntity), pageable, 1);

        given(bookRepository.findByGenreContaining(keyword, pageable)).willReturn(page);

        // when
        BookSearchResponse result = bookService.findBookBySearchType(type, keyword, currentPage);

        // then
        assertThat(result.content()).containsExactly(new BookResponse("test_001", "자바 입문", "김준호", "프로그래밍"));
        assertThat(result.page()).isZero();
        assertThat(result.size()).isEqualTo(PAGE_SIZE);
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.totalPages()).isEqualTo(1);

        verify(bookRepository).findByGenreContaining(keyword, pageable);
    }

    @Test
    @DisplayName("검색 결과가 없으면 빈 목록과 페이지 정보를 반환한다")
    void findBookBySearchType_returnsEmptyPage_whenNoBooksMatch() {
        // given
        SearchType type = SearchType.TITLE;
        String keyword = "unknown";
        int currentPage = 0;

        Pageable pageable = PageRequest.of(currentPage, PAGE_SIZE, Sort.by(Sort.Direction.ASC, "isbn"));
        Page<BookEntity> page = new PageImpl<>(List.of(), pageable, 0);

        given(bookRepository.findByTitleContaining(keyword, pageable)).willReturn(page);

        // when
        BookSearchResponse result = bookService.findBookBySearchType(type, keyword, currentPage);

        // then
        assertThat(result.content()).isEmpty();
        assertThat(result.page()).isZero();
        assertThat(result.size()).isEqualTo(PAGE_SIZE);
        assertThat(result.totalElements()).isEqualTo(0);
        assertThat(result.totalPages()).isEqualTo(0);

        verify(bookRepository).findByTitleContaining(keyword, pageable);
    }

    @Test
    @DisplayName("두 번째 페이지를 요청하면 해당 페이지의 도서 목록과 페이지 정보를 반환한다")
    void findBookBySearchType_returnsSecondPage_whenPageIsOne() {
        // given
        SearchType type = SearchType.TITLE;
        String keyword = "자바";
        int currentPage = 1;

        Pageable pageable = PageRequest.of(currentPage, PAGE_SIZE, Sort.by(Sort.Direction.ASC, "isbn"));
        Page<BookEntity> page = new PageImpl<>(
                List.of(createBook("test_005"), createBook("test_006"), createBook("test_007"), createBook("test_008"),
                        createBook("test_009")), pageable, 10);

        given(bookRepository.findByTitleContaining(keyword, pageable)).willReturn(page);

        // when
        BookSearchResponse result = bookService.findBookBySearchType(type, keyword, currentPage);

        // then
        assertThat(result.content()).containsExactly(new BookResponse("test_005", "자바 입문", "김준호", "프로그래밍"),
                new BookResponse("test_006", "자바 입문", "김준호", "프로그래밍"),
                new BookResponse("test_007", "자바 입문", "김준호", "프로그래밍"),
                new BookResponse("test_008", "자바 입문", "김준호", "프로그래밍"),
                new BookResponse("test_009", "자바 입문", "김준호", "프로그래밍"));
        assertThat(result.page()).isEqualTo(1);
        assertThat(result.size()).isEqualTo(PAGE_SIZE);
        assertThat(result.totalElements()).isEqualTo(10);
        assertThat(result.totalPages()).isEqualTo(2);

        verify(bookRepository).findByTitleContaining(keyword, pageable);
    }
}


