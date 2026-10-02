package kr.ac.hansung.kjh.bookrental.repository;

import kr.ac.hansung.kjh.bookrental.entity.RentalEntity;
import kr.ac.hansung.kjh.bookrental.enums.RentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RentalRepository extends JpaRepository<RentalEntity, Long> {
    List<RentalEntity> findByUserEmail(String email);

    List<RentalEntity> findByBookItemId(Long bookItemId);

    List<RentalEntity> findByUserEmailAndStatusIn(String email, List<RentalStatus> statuses);

    @Query("""
            select count(r) > 0 from RentalEntity r
            join BookItemEntity bi on bi.id = r.bookItemId
            where r.userEmail = :userEmail 
                and bi.isbn = :isbn
                and (r.status = 'RENTED' or r.status = 'OVERDUE') 
            """)
    boolean existsActiveRentalByUserEmailAndIsbn(
            @Param("userEmail") String userEmail,
            @Param("isbn") String isbn
    );

    @Modifying
    @Query("""
            update RentalEntity
            set returnedAt = :returnedDate, status = 'RETURNED'
            where id = :id and (status = 'RENTED' or status='OVERDUE')
            """)
    int updateReturnedDate(
            @Param("id") Long id,
            @Param("returnedDate") LocalDate returnedDate);

    @Modifying
    @Query("""
            update RentalEntity
            set status = 'OVERDUE'
            where status = 'RENTED' and dueDate < :today
            """)
    void updateOverdue(@Param("today") LocalDate today);
}