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
        when(userDao.isExist(user.getId())).thenReturn(false);
        when(encoder.encode(user.getPassword())).thenReturn("hashed_1234");

        userService.signup(user);

        verify(userDao,times(1)).add(user,"hashed_1234");
    }

    @Test
    public void loginTest(){
        User user = new User("test@naver.com","1234","test");

        when(userDao.findById(user.getId())).thenReturn(user);
        when(encoder.matches(user.getPassword(),userDao.findById(user.getId()).getPassword())).thenReturn(true);
        assertThat(userService.login(user.getId(),user.getPassword())).isTrue();
    }

    @Test
    public void loginFail_UserNotFound(){
        User user = new User("test@naver.com","1234","test");

        when(userDao.findById(user.getId())).thenReturn(null);
        assertThatThrownBy(()->userService.login(user.getId(),user.getPassword())).isInstanceOf(InvalidLoginInfoException.class);
    }

    @Test
    public void loginFail_PasswordMismatch(){
        User user = new User("test@naver.com","1234","test");
        when(userDao.findById(user.getId())).thenReturn(user);
        when(encoder.matches("1234","hashed_1234")).thenReturn(false);
        assertThatThrownBy(()->userService.login(user.getId(),user.getPassword())).isInstanceOf(InvalidLoginInfoException.class);
    }
}
