package service;

import dao.UserDao;
import domain.User;
import exception.EntityAlreadyExistsException;
import exception.EntityNotFoundException;
import exception.InvalidLoginInfoException;
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
    public void signup_NewUser_EncodePasswordAndSaveSuccess(){
        //given
        String userEmail = "test@naver.com";
        String rawPassword = "1234";
        String encodedPassword ="hashed_1234";
        String name="test";

        when(userDao.existsByEmail(userEmail)).thenReturn(false);
        when(encoder.encode(rawPassword)).thenReturn(encodedPassword);

        //when
        userService.signup(userEmail,rawPassword,name);

        //then
        verify(userDao).existsByEmail(userEmail);
        verify(encoder).encode(rawPassword);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userDao).add(userCaptor.capture());

        User saveUser = userCaptor.getValue();

        assertThat(saveUser.getEmail()).isEqualTo(userEmail);
        assertThat(saveUser.getEncodedPassword()).isEqualTo(encodedPassword);
        assertThat(saveUser.getName()).isEqualTo(name);
    }

    @Test
    public void signup_DuplicateEmail_ThrowEntityAlreadyExistsException(){
        //given
        String userEmail = "test@naver.com";
        String rawPassword = "1234";
        String name="test";

        when(userDao.existsByEmail(userEmail)).thenReturn(true);

        assertThatThrownBy(()->userService.signup(userEmail,rawPassword,name)).isInstanceOf(EntityAlreadyExistsException.class);

        verify(encoder,never()).encode(anyString());
        verify(userDao,never()).add(any(User.class));
    }

    @Test
    public void login_ValidEmailAndPassword_ReturnTrue(){
        // given
        String userEmail = "test@naver.com";
        String rawPassword = "1234";
        String encodedPassword = "hashed_1234";
        User user = new User(userEmail, encodedPassword, "test");

        when(userDao.findByEmail(userEmail)).thenReturn(user);
        when(encoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        //when
        boolean result = userService.login(userEmail, rawPassword);

        //then
        assertThat(result).isTrue();

        verify(userDao).findByEmail(userEmail);
        verify(encoder).matches(rawPassword, encodedPassword);
    }

    @Test
    public void login_InvalidPassword_ThrowInvalidLoginInfoException(){
        //given
        String userEmail = "test@naver.com";
        String rawPassword = "1234";
        String encodedPassword ="hashed_1234";
        String name="test";
        User user = new User(userEmail,encodedPassword,name);

        when(userDao.findByEmail(userEmail)).thenReturn(user);
        when(encoder.matches(rawPassword,encodedPassword)).thenReturn(false);

        //when && then
        assertThatThrownBy(()->userService.login(userEmail,rawPassword)).isInstanceOf(InvalidLoginInfoException.class);

        verify(userDao).findByEmail(userEmail);
        verify(encoder).matches(rawPassword,encodedPassword);
    }

    @Test
    public void login_NonExistentUser_ThrowInvalidLoginInfoException(){
        //given
        String userEmail = "test@naver.com";
        String rawPassword = "1234";

        when(userDao.findByEmail(userEmail))
                .thenThrow(new EntityNotFoundException(
                        "사용자를 찾을 수 없습니다."
                ));

        //when & then
        assertThatThrownBy(()->userService.login(userEmail,rawPassword)).isInstanceOf(EntityNotFoundException.class);
        verify(userDao).findByEmail(userEmail);

        verify(encoder,never()).matches(anyString(),anyString());

    }

    @Test
    public void getUser_ExistingEmail_ReturnUser(){
        //given
        String userEmail = "test@naver.com";
        String rawPassword = "1234";
        String encodedPassword ="hashed_1234";
        String name="test";
        User user = new User(userEmail,encodedPassword,name);

        when(userDao.findByEmail(userEmail)).thenReturn(user);

        //when
        User result= userService.getUser(userEmail);

        //then
        assertThat(result).isSameAs(user);
        assertThat(result.getEmail()).isEqualTo(userEmail);

        verify(userDao).findByEmail(userEmail);
    }
}
