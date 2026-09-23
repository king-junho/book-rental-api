package service;

import dao.UserDao;
import domain.User;
import exception.InvalidLoginInfoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    public void signupTest(){
        User user = new User("test@naver.com","1234","test");
        when(userDao.existsByEmail(user.getEmail())).thenReturn(false);
        when(encoder.encode(user.getEncodedPassword())).thenReturn("hashed_1234");

        userService.signup(user);

        verify(userDao,times(1)).add(user);
    }

    @Test
    public void loginTest(){
        User user = new User("test@naver.com","1234","test");

        when(userDao.findByEmail(user.getEmail())).thenReturn(user);
        when(encoder.matches(user.getEncodedPassword(),userDao.findByEmail(user.getEmail()).getEncodedPassword())).thenReturn(true);
        assertThat(userService.login(user.getEmail(),user.getEncodedPassword())).isTrue();
    }

    @Test
    public void loginFail_UserNotFound(){
        User user = new User("test@naver.com","1234","test");

        when(userDao.findByEmail(user.getEmail())).thenReturn(null);
        assertThatThrownBy(()->userService.login(user.getEmail(),user.getEncodedPassword())).isInstanceOf(InvalidLoginInfoException.class);
    }

    @Test
    public void loginFail_PasswordMismatch(){
        User user = new User("test@naver.com","1234","test");
        when(userDao.findByEmail(user.getEmail())).thenReturn(user);
        when(encoder.matches("1234","hashed_1234")).thenReturn(false);
        assertThatThrownBy(()->userService.login(user.getEmail(),user.getEncodedPassword())).isInstanceOf(InvalidLoginInfoException.class);
    }
}
