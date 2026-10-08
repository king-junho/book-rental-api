package kr.ac.hansung.kjh.bookrental.service;

import kr.ac.hansung.kjh.bookrental.entity.UserEntity;
import kr.ac.hansung.kjh.bookrental.exception.CustomException;
import kr.ac.hansung.kjh.bookrental.exception.ErrorCode;
import kr.ac.hansung.kjh.bookrental.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("중복되지 않은 이메일이면 비밀번호를 인코딩하여 회원을 저장한다")
    void signup_saveUserWithEncodedPassword_whenEmailIsAvailable() {
        // given
        String userEmail = "test@example.com";
        String password = "test_password";
        String name = "test_name";
        String encodedPassword = "test_encodedPassword";

        given(userRepository.existsByEmail(userEmail)).willReturn(false);
        given(passwordEncoder.encode(password)).willReturn(encodedPassword);

        // when
        userService.signup(userEmail, password, name);

        // then
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());
        UserEntity savedUser = captor.getValue();

        assertThat(savedUser.getEmail()).isEqualTo(userEmail);
        assertThat(savedUser.getPassword()).isEqualTo(encodedPassword);
        assertThat(savedUser.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("이메일이 중복되면 DUPLICATE_EMAIL 예외를 던지고 회원을 저장하지 않는다")
    void signup_throwsDuplicateEmail_whenEmailAlreadyExists() {
        // given
        String userEmail = "test@example.com";
        String password = "test_password";
        String name = "test_name";

        given(userRepository.existsByEmail(userEmail)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.signup(userEmail, password, name)).isInstanceOfSatisfying(
                CustomException.class,
                exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_EMAIL));

        verifyNoInteractions(passwordEncoder);
        verify(userRepository, never()).save(any(UserEntity.class));
    }
}
