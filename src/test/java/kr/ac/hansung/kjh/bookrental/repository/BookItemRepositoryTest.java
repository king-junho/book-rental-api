package kr.ac.hansung.kjh.bookrental.repository;

import jakarta.persistence.EntityManager;
import kr.ac.hansung.kjh.bookrental.entity.BookItemEntity;
import kr.ac.hansung.kjh.bookrental.enums.BookItemStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {"spring.jpa.hibernate.ddl-auto=create-drop", "spring.sql.init.mode=never"})
class BookItemRepositoryTest {
    @Autowired
    private BookItemRepository bookItemRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("상태와 관계없이 해당 ISBN의 전체 보유 권수를 반환한다")
    void countByIsbn_countsAllStatusesForMatchingIsbn() {
        // given
        for (int i = 1; i <= 5; i++) {
            bookItemRepository.save(BookItemEntity.create("test_isbn", BookItemStatus.AVAILABLE));
        }
        bookItemRepository.save(BookItemEntity.create("test_isbn", BookItemStatus.RENTED));
        bookItemRepository.save(BookItemEntity.create("test2_isbn", BookItemStatus.RENTED));
        entityManager.flush();
        entityManager.clear();

        // when
        int count = bookItemRepository.countByIsbn("test_isbn");

        // then
        assertThat(count).isEqualTo(6);
    }

    @Test
    @DisplayName("ISBN과 상태가 모두 일치하는 보유 권수를 반환한다")
    void countByIsbnAndStatus_countsOnlyMatchingBookItems() {
        // given
        for (int i = 1; i <= 5; i++) {
            bookItemRepository.save(BookItemEntity.create("test_isbn", BookItemStatus.AVAILABLE));
        }
        bookItemRepository.save(BookItemEntity.create("test_isbn", BookItemStatus.RENTED));
        bookItemRepository.save(BookItemEntity.create("test2_isbn", BookItemStatus.RENTED));
        entityManager.flush();
        entityManager.clear();

        // when
        int count = bookItemRepository.countByIsbnAndStatus("test_isbn", BookItemStatus.AVAILABLE);

        // then
        assertThat(count).isEqualTo(5);
    }

    @Test
    @DisplayName("ISBN과 상태가 모두 일치하는 보유 도서만 반환한다")
    void findByIsbnAndStatus_returnsOnlyMatchingBookItems() {
        // given
        String isbn = "test_isbn";
        List<Long> expectedIds = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            BookItemEntity saved = bookItemRepository.save(BookItemEntity.create(isbn, BookItemStatus.AVAILABLE));
            expectedIds.add(saved.getId());
        }

        bookItemRepository.save(BookItemEntity.create(isbn, BookItemStatus.RENTED));
        bookItemRepository.save(BookItemEntity.create("other_isbn", BookItemStatus.AVAILABLE));

        entityManager.flush();
        entityManager.clear();

        // when
        List<BookItemEntity> results = bookItemRepository.findByIsbnAndStatus(isbn, BookItemStatus.AVAILABLE);

        // then
        assertThat(results).extracting(BookItemEntity::getId).containsExactlyInAnyOrderElementsOf(expectedIds);
    }
}
