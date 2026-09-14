import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("/applicationContext.xml")
public class UserDaoTest {
    @Autowired private UserDao userDao;
    private User user1,user2,user3;

    @BeforeEach
    public void setUp() {
        userDao.deleteAll();
        this.user1 = new User("1","1","1");
        this.user2 = new User("2","2","2");
        this.user3 = new User("3","3","3");
    }

    @Test
    public void signUpAndLogin() {
        User user = new User("test@gmail.com","1234","테스트 계정");

        userDao.signup(user);

        User loginUser = userDao.login(user.getId(),user.getPassword());

        assertThat(loginUser).isNotNull();
        assertThat(loginUser.getId()).isEqualTo(user.getId());
        assertThat(loginUser.getPassword()).isEqualTo(user.getPassword());
        assertThat(loginUser.getName()).isEqualTo(user.getName());
    }

    @Test
    public void duplicateId(){
        userDao.signup(user1);
        assertThatThrownBy(()->userDao.signup(user1)).isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    public void getUserFailure(){
        assertThat(userDao.login("unknown","unknown")).isNull();
    }
}
