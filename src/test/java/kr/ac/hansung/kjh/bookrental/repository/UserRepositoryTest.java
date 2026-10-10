package kr.ac.hansung.kjh.bookrental.repository;

import jakarta.persistence.EntityManager;
import kr.ac.hansung.kjh.bookrental.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {"spring.jpa.hibernate.ddl-auto=create-drop", "spring.sql.init.mode=never"})
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("해당 이메일의 사용자가 존재하면 true를 반환한다")
    void existsByEmail_returnsTrue_whenEmailExists() {
        // given
        String userEmail = "test@example.com";

        UserEntity userEntity = userRepository.saveAndFlush(UserEntity.createUser(userEmail, "test_password", "test"));
        entityManager.clear();

        // when
        boolean result = userRepository.existsByEmail(userEmail);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("다른 사용자가 있어도 조회한 이메일이 없으면 false를 반환한다")
    void existsByEmail_returnsFalse_whenEmailDoesNotExist() {
        // given
        String userEmail = "test@example.com";

        userRepository.saveAndFlush(UserEntity.createUser(userEmail, "test_password", "test"));
        entityManager.clear();

        // when
        boolean result = userRepository.existsByEmail("unknown@example.com");

        // then
        assertThat(result).isFalse();
    }
}
