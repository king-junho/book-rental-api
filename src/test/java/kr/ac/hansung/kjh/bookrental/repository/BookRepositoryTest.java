package kr.ac.hansung.kjh.bookrental.repository;

import jakarta.persistence.EntityManager;
import kr.ac.hansung.kjh.bookrental.entity.BookEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {"spring.jpa.hibernate.ddl-auto=create-drop", "spring.sql.init.mode=never"})
class BookRepositoryTest {
    private static final int PAGE_SIZE = 5;
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("제목에 검색어가 포함된 도서를 ISBN 오름차순으로 첫 페이지에 반환한다")
    void findByTitleContaining_returnsFirstPageSortedByIsbn() {
        // given
        Pageable pageable = PageRequest.of(0, PAGE_SIZE, Sort.by(Sort.Direction.ASC, "isbn"));

        for (int i = 6; i >= 0; i--) {
            bookRepository.save(BookEntity.create(String.format("isbn_%03d", i), String.format("title_%03d", i),
                    String.format("author_%03d", i), String.format("genre_%03d", i)));
        }
        bookRepository.save(BookEntity.create("isbn_999", "검색불가", "검색불가", "검색불가"));

        entityManager.flush();
        entityManager.clear();

        // when
        Page<BookEntity> result = bookRepository.findByTitleContaining("title", pageable);

        // then
        assertThat(result.getContent()).extracting(BookEntity::getIsbn).containsExactly("isbn_000", "isbn_001",
                "isbn_002", "isbn_003", "isbn_004");
        assertThat(result.getNumber()).isZero();
        assertThat(result.getSize()).isEqualTo(PAGE_SIZE);
        assertThat(result.getNumberOfElements()).isEqualTo(5);
        assertThat(result.getTotalElements()).isEqualTo(7L);
        assertThat(result.getTotalPages()).isEqualTo(2);
    }


    @Test
    @DisplayName("제목에 검색어가 포함된 도서가 없으면 빈 페이지를 반환한다")
    void findByTitleContaining_returnsEmptyPage_whenNoTitleMatches() {
        // given
        Pageable pageable = PageRequest.of(0, PAGE_SIZE, Sort.by(Sort.Direction.ASC, "isbn"));

        for (int i = 6; i >= 0; i--) {
            bookRepository.save(BookEntity.create(String.format("isbn_%03d", i), String.format("title_%03d", i),
                    String.format("author_%03d", i), String.format("genre_%03d", i)));
        }
        
        entityManager.flush();
        entityManager.clear();

        // when
        Page<BookEntity> result = bookRepository.findByTitleContaining("unknown", pageable);

        // then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getNumberOfElements()).isZero();
        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getTotalPages()).isZero();
        assertThat(result.getSize()).isEqualTo(PAGE_SIZE);
    }

}
