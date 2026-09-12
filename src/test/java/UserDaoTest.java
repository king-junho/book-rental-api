import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class UserDaoTest {
    public UserDao userDao=new UserDao();

    @BeforeEach
    void setUp(){
        userDao.deleteAll();
    }

    @Test
    public void loginAndSignup() {
        User user = new User("test@gmail.com","1234","테스트 계정");

        userDao.signup(user);

        User loginUser = userDao.login(user.getId(),user.getPassword());

        assertThat(loginUser).isNotNull();
        assertThat(loginUser.getId()).isEqualTo(user.getId());
        assertThat(loginUser.getPassword()).isEqualTo(user.getPassword());
        assertThat(loginUser.getName()).isEqualTo(user.getName());
    }
}
