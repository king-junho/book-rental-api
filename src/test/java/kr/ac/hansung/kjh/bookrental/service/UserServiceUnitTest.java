package kr.ac.hansung.kjh.bookrental.service;

import kr.ac.hansung.kjh.bookrental.dao.UserDao;
import kr.ac.hansung.kjh.bookrental.domain.User;
import kr.ac.hansung.kjh.bookrental.entity.UserEntity;
import kr.ac.hansung.kjh.bookrental.exception.EntityAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {
    @Mock
    private UserDao userDao;
    @Mock
    private PasswordEncoder encoder;
    @InjectMocks
    private UserService userService;

    @Test
    public void signup_NewUser_EncodePasswordAndSaveSuccess() {
        //given
        String userEmail = "test@naver.com";
        String rawPassword = "1234";
        String encodedPassword = "hashed_1234";
        String name = "test";


        when(userDao.existsByEmail(userEmail)).thenReturn(false);
        when(encoder.encode(rawPassword)).thenReturn(encodedPassword);


        //when
        userService.signup(userEmail, rawPassword, name);

        //then
        verify(userDao).existsByEmail(userEmail);
        verify(encoder).encode(rawPassword);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userDao).add(userCaptor.capture());

        User saveUser = userCaptor.getValue();

        assertThat(saveUser.email()).isEqualTo(userEmail);
        assertThat(saveUser.encodedPassword()).isEqualTo(encodedPassword);
        assertThat(saveUser.name()).isEqualTo(name);
    }

    @Test
    public void signup_DuplicateEmail_ThrowEntityAlreadyExistsException() {
        //given
        String userEmail = "test@naver.com";
        String rawPassword = "1234";
        String name = "test";

        when(userDao.existsByEmail(userEmail)).thenReturn(true);

        assertThatThrownBy(() -> userService.signup(userEmail, rawPassword, name)).isInstanceOf(EntityAlreadyExistsException.class);

        verify(encoder, never()).encode(anyString());
        verify(userDao, never()).add(any(User.class));
    }

    @Test
    public void getUser_ExistingEmail_ReturnUser() {
        //given
        String userEmail = "test@naver.com";
        String encodedPassword = "hashed_1234";
        String name = "test";
        User user = new User(userEmail, encodedPassword, name);

        when(userDao.findByEmail(userEmail)).thenReturn(user);

        //when
        UserEntity result = userService.getUser(userEmail);

        //then
        assertThat(result).isSameAs(user);
        assertThat(result.getEmail()).isEqualTo(userEmail);

        verify(userDao).findByEmail(userEmail);
    }
}
