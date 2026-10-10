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
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest(properties = {"spring.jpa.hibernate.ddl-auto=create-drop", "spring.sql.init.mode=never"})
@Import({RentalService.class, RentalRetryFacade.class, RentalServiceIntegrationTest.FixedClockConfig.class})
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
    private RentalRetryFacade rentalRetryFacade;

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

    @Test
    @DisplayName("책 한 권을 여러명이 동시에 대여를 요청하면 한 명만 성공한다")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void rentBook_allowsOnlyOneSuccess_whenUsersCompeteForOneCopy() throws Exception {
        // given
        String isbn = "isbn";
        int requestCount = 5;

        List<String> userEmails = IntStream.range(0, requestCount).mapToObj(
                i -> String.format("test%03d@example.com", i)).toList();

        saveBook(isbn);

        BookItemEntity bookItem = saveBookItem(isbn, BookItemStatus.AVAILABLE);
        Long bookItemId = bookItem.getId();

        CountDownLatch ready = new CountDownLatch(requestCount);
        CountDownLatch start = new CountDownLatch(1);

        ExecutorService executor = Executors.newFixedThreadPool(requestCount);
        List<Future<Boolean>> futures = new ArrayList<>();

        try {
            for (String userEmail : userEmails) {
                futures.add(executor.submit(() -> {
                    ready.countDown();

                    if (!start.await(5, SECONDS)) {
                        throw new AssertionError("대여 시작 신호를 받지 못했습니다.");
                    }
                    try {
                        rentalRetryFacade.rentBookWithRetry(userEmail, isbn);
                        return true;
                    } catch (CustomException e) {
                        if (e.getErrorCode() == ErrorCode.BOOK_OUT_OF_STOCK) {
                            return false;
                        }
                        throw e;
                    }
                }));
            }

            // when
            assertThat(ready.await(5, SECONDS)).as("모든 대여 요청이 실행 준비를 마쳤는지").isTrue();

            start.countDown();

            int successCount = 0;

            for (Future<Boolean> future : futures) {
                if (future.get(10, SECONDS)) {
                    successCount++;
                }
            }

            // then
            assertThat(successCount).isEqualTo(1);

            BookItemEntity actualBookItem = bookItemRepository.findById(bookItemId).orElseThrow();

            assertThat(actualBookItem.getStatus()).isEqualTo(BookItemStatus.RENTED);

            List<RentalEntity> savedRentals = userEmails.stream().flatMap(
                    email -> rentalRepository.findByUserEmail(email).stream()).toList();

            assertThat(savedRentals).hasSize(1);
            assertThat(savedRentals.get(0).getBookItemId()).isEqualTo(bookItemId);
            assertThat(savedRentals.get(0).getStatus()).isEqualTo(RentalStatus.RENTED);
        } finally {
            start.countDown();
            executor.shutdown();

            boolean terminated = executor.awaitTermination(5, SECONDS);

            assertThat(terminated).as("DB 정리 전에 작업 스레드가 모두 종료됐는지").isTrue();

            for (String userEmail : userEmails) {
                rentalRepository.deleteAll(rentalRepository.findByUserEmail(userEmail));
            }

            bookItemRepository.deleteById(bookItemId);
            bookRepository.deleteById(isbn);
        }
    }

    @Test
    @DisplayName("반납 동시성 제어")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void returnBook_allowsOnlyOneSuccess_whenUserCompeteForOneCopy() throws Exception {
        // given
        String isbn = "isbn";
        String userEmail = "test@example.com";
        int requestCount = 2;

        saveBook(isbn);

        BookItemEntity bookItem = saveBookItem(isbn, BookItemStatus.RENTED);
        Long bookItemId = bookItem.getId();

        RentalEntity rental = rentalRepository.save(RentalEntity.create(userEmail, bookItemId, TODAY.minusDays(5)));
        Long rentalId = rental.getId();

        long bookItemVersion = bookItem.getVersion();
        long rentalVersion = rental.getVersion();

        CountDownLatch ready = new CountDownLatch(requestCount);
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(requestCount);
        List<Future<Boolean>> futures = new ArrayList<>();

        try {
            for (int i = 0; i < requestCount; i++) {
                futures.add(executor.submit(() -> {
                    ready.countDown();

                    if (!start.await(5, SECONDS)) {
                        throw new AssertionError("반납 신호를 받지 못했습니다.");
                    }
                    try {
                        rentalService.returnBook(rentalId, userEmail);
                        return true;
                    } catch (OptimisticLockingFailureException e) {
                        return false;
                    } catch (CustomException e) {
                        if (e.getErrorCode() == ErrorCode.RENTAL_ALREADY_RETURNED) {
                            return false;
                        }
                        throw e;
                    }
                }));
            }
            // when
            assertThat(ready.await(5, SECONDS)).isTrue();
            start.countDown();

            int successCount = 0;

            for (Future<Boolean> future : futures) {
                if (future.get(10, SECONDS)) {
                    successCount++;
                }
            }

            // then
            assertThat(successCount).isEqualTo(1);

            BookItemEntity actualBookItem = bookItemRepository.findById(bookItemId).orElseThrow();
            assertThat(actualBookItem.getStatus()).isEqualTo(BookItemStatus.AVAILABLE);

            RentalEntity actualRental = rentalRepository.findById(rentalId).orElseThrow();
            assertThat(actualRental.getStatus()).isEqualTo(RentalStatus.RETURNED);
            assertThat(actualRental.getReturnedAt()).isEqualTo(TODAY);

            assertThat(actualBookItem.getVersion()).isEqualTo(bookItemVersion + 1);
            assertThat(actualRental.getVersion()).isEqualTo(rentalVersion + 1);
        } finally {
            start.countDown();
            executor.shutdown();

            boolean terminated = executor.awaitTermination(5, SECONDS);
            if (!terminated) {
                executor.shutdownNow();
                terminated = executor.awaitTermination(5, SECONDS);
            }

            assertThat(terminated).as("DB 정리 전에 스레드 모두 종료 됐는지").isTrue();

            rentalRepository.deleteById(rentalId);
            bookItemRepository.deleteById(bookItemId);
            bookRepository.deleteById(isbn);
        }
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
