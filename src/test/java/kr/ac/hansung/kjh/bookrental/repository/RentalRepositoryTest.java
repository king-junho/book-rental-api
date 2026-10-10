package kr.ac.hansung.kjh.bookrental.repository;

import jakarta.persistence.EntityManager;
import kr.ac.hansung.kjh.bookrental.entity.RentalEntity;
import kr.ac.hansung.kjh.bookrental.enums.RentalStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {"spring.jpa.hibernate.ddl-auto=create-drop", "spring.sql.init.mode=never"})
class RentalRepositoryTest {
    private static final LocalDate TODAY = LocalDate.of(2026, 10, 5);
    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("반납 예정일이 지난 미반납 대여만 연체 처리한다")
    void updateOverdue_updatesOnlyRentalsPastDueDate() {
        // given
        RentalEntity overdueRental = RentalEntity.create("overdue@example.com", 1L, TODAY.minusDays(15));
        RentalEntity dueTodayRental = RentalEntity.create("today@example.com", 2L, TODAY.minusDays(14));
        RentalEntity returnedRental = RentalEntity.create("returned@example.com", 3L, TODAY.minusDays(20));

        returnedRental.returnBook(TODAY.minusDays(1));

        rentalRepository.save(overdueRental);
        rentalRepository.save(dueTodayRental);
        rentalRepository.save(returnedRental);

        entityManager.flush();
        entityManager.clear();

        // when
        int updatedCount = rentalRepository.updateOverdue(TODAY);

        entityManager.clear();

        // then
        RentalEntity actualOverdue = rentalRepository.findById(overdueRental.getId()).orElseThrow();
        RentalEntity actualDueToday = rentalRepository.findById(dueTodayRental.getId()).orElseThrow();
        RentalEntity actualReturned = rentalRepository.findById(returnedRental.getId()).orElseThrow();

        assertThat(updatedCount).isEqualTo(1);
        assertThat(actualOverdue.getStatus()).isEqualTo(RentalStatus.OVERDUE);
        assertThat(actualDueToday.getStatus()).isEqualTo(RentalStatus.RENTED);
        assertThat(actualReturned.getStatus()).isEqualTo(RentalStatus.RETURNED);
    }

    @Test
    @DisplayName("같은 날짜로 연체 갱신을 다시 실행하면 추가 변경하지 않는다")
    void updateOverdue_doesNotUpdateAgain_whenAlreadyOverdue() {
        // given
        RentalEntity rentalEntity = rentalRepository.saveAndFlush(
                RentalEntity.create("test@example.com", 1L, TODAY.minusDays(15)));

        Long id = rentalEntity.getId();
        Long initialVersion = rentalEntity.getVersion();
        entityManager.clear();

        // when
        int firstCount = rentalRepository.updateOverdue(TODAY);
        int secondCount = rentalRepository.updateOverdue(TODAY);
        entityManager.clear();

        // then
        RentalEntity actualEntity = rentalRepository.findById(id).orElseThrow();

        assertThat(firstCount).isEqualTo(1);
        assertThat(secondCount).isZero();
        assertThat(actualEntity.getStatus()).isEqualTo(RentalStatus.OVERDUE);
        assertThat(actualEntity.getVersion()).isEqualTo(initialVersion + 1);
    }

    @Test
    @DisplayName("이메일에 해당하는 대여 기록만 조회")
    void findByUserEmail_listRentalEntity_whenUserEmailExists() {
        // given
        String userEmail = "test1@example.com";

        RentalEntity firstRentalEntity = RentalEntity.create(userEmail, 1L, TODAY);
        RentalEntity secondRentalEntity = RentalEntity.create(userEmail, 2L, TODAY);
        RentalEntity otherRentalEntity = RentalEntity.create("test2@example.com", 3L, TODAY);

        rentalRepository.save(firstRentalEntity);
        rentalRepository.save(secondRentalEntity);
        entityManager.flush();
        entityManager.clear();

        // when
        List<RentalEntity> results = rentalRepository.findByUserEmail(userEmail);

        // then
        assertThat(results).extracting(RentalEntity::getId).containsExactlyInAnyOrder(firstRentalEntity.getId(),
                secondRentalEntity.getId());
        assertThat(results).extracting(RentalEntity::getUserEmail).containsOnly(userEmail);
    }

    @Test
    @DisplayName("해당 이메일의 대여 기록이 없으면 빈 목록을 반환한다")
    void findByUserEmail_returnsEmptyList_whenNoRentalsExist() {
        //when
        List<RentalEntity> results = rentalRepository.findByUserEmail("test1@example.com");

        // then
        assertThat(results).isEmpty();
    }
}
