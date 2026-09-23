package dao;

import domain.User;
import exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("/userDaoTestContext.xml")
public class UserDaoTest {

    @Autowired private UserDao userDao;
    @Autowired private TestDataCleaner testDataCleaner;

    @BeforeEach
    public void setUp() {
        testDataCleaner.cleanUp();
    }

    private User setData(){
        User user = new User("test@test.com","hashed_password","test");
        userDao.add(user);

        return user;
    }
    @Test
    public void add_UserAndFindUser(){
        User user = setData();

        User findUser = userDao.findByEmail(user.getEmail());
        assertThat(findUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(findUser.getEncodedPassword()).isEqualTo(user.getEncodedPassword());
        assertThat(user.getName()).isEqualTo(findUser.getName());
    }

    @Test
    public void find_ByEmail_ThrowsException_WhenUserDoesNotExist(){
        assertThatThrownBy(()->userDao.findByEmail("unknown")).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    public void exists_ByEmail_ReturnsTrue_WhenUserExists(){
        User user = setData();

        assertThat(userDao.existsByEmail(user.getEmail())).isTrue();
    }

    @Test
    public void exists_ByEmail_ReturnsFalse_WhenUserDoesNotExist(){
        assertThat(userDao.existsByEmail("unknown")).isFalse();
    }
}
