package kr.ac.hansung.kjh.bookrental.repository;

import kr.ac.hansung.kjh.bookrental.entity.RentalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RentalRepository extends JpaRepository<RentalEntity, Long> {
    List<RentalEntity> findByUserEmail(String email);

    @Modifying
    @Query("""
            update RentalEntity r
            set r.status = 'OVERDUE',
            r.version = r.version + 1
            where r.status = 'RENTED'
              and r.returnedAt is null
              and r.dueDate < :today
            """)
    int updateOverdue(@Param("today") LocalDate today);
}