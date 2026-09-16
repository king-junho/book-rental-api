package dao;

import domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("/userDaoTestContext.xml")
public class UserDaoTest {

    @Autowired private UserDao userDao;

    @BeforeEach
    public void setUp() {
        userDao.deleteAll();
    }

    @Test
    public void addUserAndFindUser(){
        User user = new User("test@test.com","1234","test");
        userDao.add(user,user.getPassword());

        User findUser = userDao.findById(user.getId());
        assertThat(findUser.getId()).isEqualTo(user.getId());
        assertThat(findUser.getPassword()).isEqualTo(user.getPassword()); //hashed처리는 서비스 계층에서 처리
        assertThat(user.getName()).isEqualTo(findUser.getName());
    }

    @Test
    public void deleteAllUsersAndExistsUser(){
        User user = new User("test@test.com","1234","test");
        userDao.add(user, user.getPassword());
        assertThat(userDao.isExist(user.getId())).isTrue();

        userDao.deleteAll();
        assertThat(userDao.isExist(user.getId())).isFalse();
    }
}
