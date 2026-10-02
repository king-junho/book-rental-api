package kr.ac.hansung.kjh.bookrental.repository;

import kr.ac.hansung.kjh.bookrental.entity.BookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<BookEntity, String> {
    Page<BookEntity> findByTitleContaining(String title, Pageable pageable);

    Page<BookEntity> findByAuthorContaining(String author, Pageable pageable);

    Page<BookEntity> findByGenreContaining(String genre, Pageable pageable);
}