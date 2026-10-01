package kr.ac.hansung.kjh.bookrental.repository;

import kr.ac.hansung.kjh.bookrental.domain.enums.BookItemStatus;
import kr.ac.hansung.kjh.bookrental.entity.BookItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface BookItemRepository extends JpaRepository<BookItemEntity, Long> {
    int countByIsbn(String isbn);

    int countByIsbnAndStatus(String isbn, BookItemStatus status);

    List<BookItemEntity> findByIsbnAndStatus(String isbn, BookItemStatus status);

    @Modifying
    @Query("""
            update BookItemEntity 
            set status = 'RENTED'
            where id=:bookItemId and status = 'AVAILABLE'
            """)
    int rent(@Param("bookItemId") Long bookItemId);

    @Modifying
    @Query("""
            update BookItemEntity 
            set status = 'AVAILABLE'
            where id = :id and status = 'RENTED'
            """)
    int returnBook(@Param("id") Long id);
}
