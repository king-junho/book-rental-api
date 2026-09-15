import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("/userDaoTestContext.xml")
public class UserDaoTest {
    @Autowired private UserService userService;
    private User user1,user2,user3;

    @BeforeEach
    public void setUp() {
        userService.deleteAll();

        this.user1 = new User("test1@gmail.com","1234","테스트 계정 1");
        this.user2 = new User("test2@gmail.com","1234","테스트 계정 2");
        this.user3 = new User("test3@gmail.com","1234","테스트 계정 3");

        userService.signup(user1);
        userService.signup(user2);
        userService.signup(user3);
    }

    @Test
    public void signUpAndLogin() {
        User loginUser = userService.login(user1.getId(),user1.getPassword());

        assertThat(loginUser).isNotNull();
        assertThat(loginUser.getId()).isEqualTo(user1.getId());
        assertThat(loginUser.getName()).isEqualTo(user1.getName());
    }

    @Test
    public void duplicateId(){
        assertThatThrownBy(()->userService.signup(user1)).isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    public void getUserLoginFailure(){
        assertThatThrownBy(()->userService.login("unknown","unknown")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void invalidEmailFormId(){
        assertThatThrownBy(()->{
            userService.signup(new User("1","1","1"));
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void passwordShouldBeEncrypted(){
        User loginUser =  userService.login(user1.getId(),user1.getPassword());
        assertThat(loginUser).isNotNull();

        String dbPassword = loginUser.getPassword();
        assertThat(dbPassword).isNotEqualTo(user1.getPassword());

        assertThat(dbPassword).startsWith("$2a$");
    }
}
