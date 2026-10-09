package kr.ac.hansung.kjh.bookrental.service;

import kr.ac.hansung.kjh.bookrental.dto.response.RentalResponse;
import kr.ac.hansung.kjh.bookrental.entity.BookItemEntity;
import kr.ac.hansung.kjh.bookrental.entity.RentalEntity;
import kr.ac.hansung.kjh.bookrental.enums.BookItemStatus;
import kr.ac.hansung.kjh.bookrental.enums.RentalStatus;
import kr.ac.hansung.kjh.bookrental.exception.CustomException;
import kr.ac.hansung.kjh.bookrental.exception.ErrorCode;
import kr.ac.hansung.kjh.bookrental.repository.BookItemRepository;
import kr.ac.hansung.kjh.bookrental.repository.RentalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class RentalServiceTest {
    private static final LocalDate TODAY = LocalDate.of(2026, 10, 5);
    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private BookItemRepository bookItemRepository;

    private RentalService rentalService;

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(TODAY.atStartOfDay(ZONE).toInstant(), ZONE);

        rentalService = new RentalService(rentalRepository, bookItemRepository, fixedClock);
    }

    @Test
    @DisplayName("대여 기록이 있으면 각 기록을 응답 DTO로 변환하여 반환한다")
    void retrieveUserRentalHistory_returnsResponses_whenRentalsExist() {
        // given
        String userEmail = "text@example.com";
        RentalEntity firstRental = RentalEntity.create(userEmail, 1L, TODAY);
        RentalEntity secondRental = RentalEntity.create(userEmail, 2L, TODAY);

        given(rentalRepository.findByUserEmail(userEmail)).willReturn(List.of(firstRental, secondRental));

        RentalResponse expectedFirst = new RentalResponse(firstRental.getId(), 1L, firstRental.getRentedAt(),
                firstRental.getDueDate(), null, RentalStatus.RENTED);

        RentalResponse expectedSecond = new RentalResponse(secondRental.getId(), 2L, secondRental.getRentedAt(),
                secondRental.getDueDate(), null, RentalStatus.RENTED);

        // when
        List<RentalResponse> result = rentalService.retrieveUserRentalHistory(userEmail);

        // then
        assertThat(result).containsExactly(expectedFirst, expectedSecond);

        verify(rentalRepository).findByUserEmail(userEmail);
    }

    @Test
    @DisplayName("대여 기록이 없으면 빈 목록을 반환한다")
    void retrieveUserRentalHistory_returnsEmptyList_whenNoRentalsExist() {
        // given
        String userEmail = "text@example.com";
        given(rentalRepository.findByUserEmail(userEmail)).willReturn(List.of());

        // when
        List<RentalResponse> result = rentalService.retrieveUserRentalHistory(userEmail);

        // then
        assertThat(result).isEmpty();
        verify(rentalRepository).findByUserEmail(userEmail);
    }

    @Test
    @DisplayName("대여 가능한 사본 중 첫 번째 사본을 대여 상태로 변경하고 대여 기록을 저장한다")
    void rentBook_updatesFirstItemAndSavesRental_whenAvailableItemsExist() {
        // given
        String userEmail = "test@example.com";
        String isbn = "test_isbn";

        BookItemEntity firstBookItemEntity = BookItemEntity.create(1L, isbn, BookItemStatus.AVAILABLE);
        BookItemEntity secondBookItemEntity = BookItemEntity.create(2L, isbn, BookItemStatus.AVAILABLE);

        given(bookItemRepository.findByIsbnAndStatus(isbn, BookItemStatus.AVAILABLE)).willReturn(
                List.of(firstBookItemEntity, secondBookItemEntity));

        // when
        rentalService.rentBook(userEmail, isbn);

        // then
        assertThat(firstBookItemEntity.getStatus()).isEqualTo(BookItemStatus.RENTED);
        assertThat(secondBookItemEntity.getStatus()).isEqualTo(BookItemStatus.AVAILABLE);

        ArgumentCaptor<RentalEntity> captor = ArgumentCaptor.forClass(RentalEntity.class);
        verify(rentalRepository).save(captor.capture());

        RentalEntity savedRental = captor.getValue();

        assertThat(savedRental.getUserEmail()).isEqualTo(userEmail);
        assertThat(savedRental.getBookItemId()).isEqualTo(1L);
        assertThat(savedRental.getStatus()).isEqualTo(RentalStatus.RENTED);
        assertThat(savedRental.getRentedAt()).isEqualTo(TODAY);
        assertThat(savedRental.getDueDate()).isEqualTo(TODAY.plusDays(14));
        assertThat(savedRental.getReturnedAt()).isNull();
    }

    @Test
    @DisplayName("대여 가능한 사본이 없으면 BOOK_OUT_OF_STOCK 예외를 던지고 저장하지 않는다")
    void rentBook_throwsBookOutOfStock_whenNoAvailableItemsExist() {
        // given
        String userEmail = "example.com";
        String isbn = "test_isbn";

        given(bookItemRepository.findByIsbnAndStatus(isbn, BookItemStatus.AVAILABLE)).willReturn(List.of());

        // when & then
        assertThatThrownBy(() -> rentalService.rentBook(userEmail, isbn)).isInstanceOfSatisfying(CustomException.class,
                exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.BOOK_OUT_OF_STOCK));

        verifyNoInteractions(rentalRepository);
    }

    @Test
    @DisplayName("본인이 대여한 도서를 반납할 경우 도서 상태 변화와 대여 정보 수정")
    void returnBook_updatesItemAndRental_whenRequestedByOwner() {
        // given
        Long rentalId = 10L;
        Long bookItemId = 1L;
        String userEmail = "test@example.com";
        RentalEntity rentalEntity = RentalEntity.create(userEmail, bookItemId, TODAY);
        BookItemEntity bookItemEntity = BookItemEntity.create(bookItemId, "test_isbn", BookItemStatus.RENTED);

        given(rentalRepository.findById(rentalId)).willReturn(Optional.of(rentalEntity));
        given(bookItemRepository.findById(bookItemId)).willReturn(Optional.of(bookItemEntity));

        // when
        rentalService.returnBook(rentalId, userEmail);

        // then
        assertThat(bookItemEntity.getStatus()).isEqualTo(BookItemStatus.AVAILABLE);
        assertThat(rentalEntity.getStatus()).isEqualTo(RentalStatus.RETURNED);
        assertThat(rentalEntity.getReturnedAt()).isEqualTo(TODAY);
    }

    @Test
    @DisplayName("대여 정보가 없는 도서를 반납할 경우 RENTAL_NOT_FOUND 예외 호출")
    void returnBook_throwsRentalNotFound_whenRentalDoesNotExist() {
        // given
        String userEmail = "example.com";
        given(rentalRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> rentalService.returnBook(1L, userEmail)).isInstanceOfSatisfying(CustomException.class,
                exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RENTAL_NOT_FOUND));

        verifyNoInteractions(bookItemRepository);
    }


    @Test
    @DisplayName("본인이 대여하지 않은 도서를 반납할 경우 RENTAL_ACCESS_DENIED 예외 호출")
    void returnBook_throwsRentalAccessDenied_whenRequestedByAnotherUser() {
        // given
        Long bookItemId = 1L;
        String ownerEmail = "owner@example.com";
        String requesterEmail = "other@example.com";
        RentalEntity rentalEntity = RentalEntity.create(ownerEmail, bookItemId, TODAY);

        given(rentalRepository.findById(1L)).willReturn(Optional.of(rentalEntity));

        // when & then
        assertThatThrownBy(() -> rentalService.returnBook(1L, requesterEmail)).isInstanceOfSatisfying(
                CustomException.class,
                exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RENTAL_ACCESS_DENIED));
        verifyNoInteractions(bookItemRepository);
        assertThat(rentalEntity.getStatus()).isEqualTo(RentalStatus.RENTED);
        assertThat(rentalEntity.getReturnedAt()).isNull();
    }

    @Test
    @DisplayName("대여한 사본이 없으면 BOOKITEM_NOT_FOUND 예외를 던지고 대여 기록을 변경하지 않는다")
    void returnBook_throwsBookItemNotFound_whenBookItemDoesNotExist() {
        // given
        Long rentalId = 10L;
        Long bookItemId = 1L;
        String userEmail = "test@example.com";

        RentalEntity rentalEntity = RentalEntity.create(userEmail, bookItemId, TODAY);

        given(rentalRepository.findById(rentalId)).willReturn(Optional.of(rentalEntity));
        given(bookItemRepository.findById(bookItemId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> rentalService.returnBook(rentalId, userEmail)).isInstanceOfSatisfying(
                CustomException.class,
                exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.BOOKITEM_NOT_FOUND));
        assertThat(rentalEntity.getStatus()).isEqualTo(RentalStatus.RENTED);
        assertThat(rentalEntity.getReturnedAt()).isNull();
    }

    @Test
    @DisplayName("현재 날짜를 기준으로 연체 갱신을 요청한다")
    void markOverdue_updatesRentals_usingCurrentDate() {
        // when
        rentalService.markOverdue();

        // then
        verify(rentalRepository).updateOverdue(TODAY);
    }
}
