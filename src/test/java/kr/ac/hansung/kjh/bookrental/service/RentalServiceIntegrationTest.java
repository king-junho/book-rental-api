package kr.ac.hansung.kjh.bookrental.service;

import jakarta.persistence.EntityManager;
import kr.ac.hansung.kjh.bookrental.entity.BookEntity;
import kr.ac.hansung.kjh.bookrental.entity.BookItemEntity;
import kr.ac.hansung.kjh.bookrental.entity.RentalEntity;
import kr.ac.hansung.kjh.bookrental.enums.BookItemStatus;
import kr.ac.hansung.kjh.bookrental.enums.RentalStatus;
import kr.ac.hansung.kjh.bookrental.exception.CustomException;
import kr.ac.hansung.kjh.bookrental.exception.ErrorCode;
import kr.ac.hansung.kjh.bookrental.repository.BookItemRepository;
import kr.ac.hansung.kjh.bookrental.repository.BookRepository;
import kr.ac.hansung.kjh.bookrental.repository.RentalRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest(properties = {"spring.jpa.hibernate.ddl-auto=create-drop", "spring.sql.init.mode=never"})
@Import({RentalService.class, RentalServiceIntegrationTest.FixedClockConfig.class})
class RentalServiceIntegrationTest {
    private static final LocalDate TODAY = LocalDate.of(2026, 10, 10);

    @Autowired
    private RentalService rentalService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookItemRepository bookItemRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private EntityManager entityManager;

    @TestConfiguration(proxyBeanMethods = false)
    static class FixedClockConfig {
        @Bean
        Clock clock() {
            ZoneId zone = ZoneId.of("Asia/Seoul");

            return Clock.fixed(TODAY.atStartOfDay(zone).toInstant(), zone);
        }
    }

    @Test
    @DisplayName("대여 가능한 도서가 여러 권이어도 한 권만 대여하고 기록을 저장한다")
    void rentBook_rentsOnlyOneCopy_whenMultipleCopiesAreAvailable() {
        // given
        String userEmail = "test@example.com";
        String isbn = "test_isbn";

        saveBook(isbn);
        saveBookItem(isbn, BookItemStatus.AVAILABLE);
        saveBookItem(isbn, BookItemStatus.AVAILABLE);

        flushAndClear();

        // when
        rentalService.rentBook(userEmail, isbn);

        flushAndClear();

        // then
        List<BookItemEntity> rentedItems = bookItemRepository.findByIsbnAndStatus(isbn, BookItemStatus.RENTED);
        List<BookItemEntity> availableItems = bookItemRepository.findByIsbnAndStatus(isbn, BookItemStatus.AVAILABLE);

        assertThat(rentedItems).hasSize(1);
        assertThat(availableItems).hasSize(1);

        List<RentalEntity> rentals = rentalRepository.findByUserEmail(userEmail);
        assertThat(rentals).hasSize(1);

        RentalEntity actualRental = rentals.get(0);

        assertThat(actualRental.getId()).isNotNull();
        assertThat(actualRental.getUserEmail()).isEqualTo(userEmail);
        assertThat(actualRental.getBookItemId()).isEqualTo(rentedItems.get(0).getId());
        assertThat(actualRental.getStatus()).isEqualTo(RentalStatus.RENTED);
        assertThat(actualRental.getRentedAt()).isEqualTo(TODAY);
        assertThat(actualRental.getDueDate()).isEqualTo(TODAY.plusDays(14));
        assertThat(actualRental.getReturnedAt()).isNull();
    }

    @Test
    @DisplayName("재고가 없으면 예외가 발생하고 대여 기록이 저장되지 않는다.")
    void rentBook_throwsOutOfStockAndSavesNothing_whenNoStock() {
        // given
        String isbn = "test_isbn";
        String userEmail = "test@example.com";
        saveBook(isbn);

        flushAndClear();

        // when & then
        assertThatThrownBy(() -> rentalService.rentBook(userEmail, isbn)).isInstanceOfSatisfying(CustomException.class,
                exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.BOOK_OUT_OF_STOCK));
        assertThat(rentalRepository.findByUserEmail(userEmail)).isEmpty();
    }

    @Test
    @DisplayName("보유 도서가 모두 대여 중 → 재고 없음")
    void rentBook_throwsOutOfStockAndSavesNothing_whenAllRented() {
        // given
        String isbn = "test_isbn";
        String userEmail = "test@example.com";

        saveBook(isbn);
        saveBookItem(isbn, BookItemStatus.RENTED);
        saveBookItem(isbn, BookItemStatus.RENTED);

        flushAndClear();

        // when & then
        assertThatThrownBy(() -> rentalService.rentBook(userEmail, isbn)).isInstanceOfSatisfying(CustomException.class,
                exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.BOOK_OUT_OF_STOCK));
        assertThat(rentalRepository.findByUserEmail(userEmail)).isEmpty();
    }

    @Test
    @DisplayName("본인의 대여 도서를 반납하면 도서 상태와 대여 기록을 변경한다")
    void returnBook_updatesBookItemAndRental_whenRentalIsActive() {
        // given
        String isbn = "test_isbn";
        String userEmail = "test@example.com";

        saveBook(isbn);
        BookItemEntity bookItem = saveBookItem(isbn, BookItemStatus.RENTED);
        Long bookItemId = bookItem.getId();

        RentalEntity rental = rentalRepository.save(RentalEntity.create(userEmail, bookItemId, TODAY));
        Long rentalId = rental.getId();

        flushAndClear();

        // when
        rentalService.returnBook(rentalId, userEmail);

        flushAndClear();

        // then
        BookItemEntity actualBookItem = bookItemRepository.findById(bookItemId).orElseThrow();
        assertThat(actualBookItem.getStatus()).isEqualTo(BookItemStatus.AVAILABLE);

        RentalEntity actualRental = rentalRepository.findById(rentalId).orElseThrow();
        assertThat(actualRental.getStatus()).isEqualTo(RentalStatus.RETURNED);
        assertThat(actualRental.getReturnedAt()).isEqualTo(TODAY);
    }

    @Test
    @DisplayName("이미 반납된 대여를 다시 반납하면 예외가 발생한다")
    void returnBook_throwsAlreadyReturned_whenRentalIsAlreadyReturned() {
        // given
        String isbn = "test_isbn";
        String userEmail = "test@example.com";
        LocalDate previousReturnDate = TODAY.minusDays(1);

        saveBook(isbn);
        BookItemEntity bookItem = saveBookItem(isbn, BookItemStatus.AVAILABLE);
        Long bookItemId = bookItem.getId();
        RentalEntity rental = rentalRepository.save(RentalEntity.create(userEmail, bookItemId, TODAY.minusDays(5)));
        rental.returnBook(previousReturnDate);

        rentalRepository.save(rental);
        Long rentalId = rental.getId();

        flushAndClear();

        // when & then
        assertThatThrownBy(() -> rentalService.returnBook(rentalId, userEmail)).isInstanceOfSatisfying(
                CustomException.class,
                exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RENTAL_ALREADY_RETURNED));
    }

    @Test
    @DisplayName("반납한 도서를 재대여하면 기존 반납 이력을 유지하고 새 대여 기록을 생성한다")
    void rentBook_createsNewRentalAndPreservesHistory_afterReturn() {
        // given
        String isbn = "test_isbn";
        String title = "test_title";
        String author = "test_author";
        String genre = "test_genre";
        String userEmail = "test@example.com";

        saveBook(isbn);
        BookItemEntity bookItem = saveBookItem(isbn, BookItemStatus.AVAILABLE);
        Long bookItemId = bookItem.getId();

        flushAndClear();

        // when
        rentalService.rentBook(userEmail, isbn);
        flushAndClear();


        List<RentalEntity> firstRentals = rentalRepository.findByUserEmail(userEmail);

        assertThat(firstRentals).hasSize(1);

        Long firstRentalId = firstRentals.get(0).getId();

        rentalService.returnBook(firstRentalId, userEmail);
        flushAndClear();

        rentalService.rentBook(userEmail, isbn);
        flushAndClear();

        // then
        BookItemEntity actualBookItem = bookItemRepository.findById(bookItemId).orElseThrow();
        List<RentalEntity> rentals = rentalRepository.findByUserEmail(userEmail);

        assertThat(rentals).hasSize(2);
        assertThat(rentals).extracting(RentalEntity::getStatus).containsExactlyInAnyOrder(RentalStatus.RENTED,
                RentalStatus.RETURNED);
        assertThat(rentals).extracting(RentalEntity::getBookItemId).containsOnly(bookItemId);

        assertThat(actualBookItem.getStatus()).isEqualTo(BookItemStatus.RENTED);
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    private void saveBook(String isbn) {
        bookRepository.save(BookEntity.create(isbn, "test_title", "test_author", "test_genre"));
    }

    private BookItemEntity saveBookItem(String isbn, BookItemStatus status) {
        return bookItemRepository.save(BookItemEntity.create(isbn, status));
    }
}
