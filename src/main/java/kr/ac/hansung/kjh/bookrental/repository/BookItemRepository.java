package kr.ac.hansung.kjh.bookrental.repository;

import kr.ac.hansung.kjh.bookrental.entity.BookItemEntity;
import kr.ac.hansung.kjh.bookrental.enums.BookItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface BookItemRepository extends JpaRepository<BookItemEntity, Long> {
    int countByIsbn(String isbn);

    int countByIsbnAndStatus(String isbn, BookItemStatus status);

    List<BookItemEntity> findByIsbnAndStatus(String isbn, BookItemStatus status);
}