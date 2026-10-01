//package kr.ac.hansung.kjh.bookrental.dao;
//
//import kr.ac.hansung.kjh.bookrental.TestDataCleaner;
//import kr.ac.hansung.kjh.bookrental.domain.User;
//import kr.ac.hansung.kjh.bookrental.exception.EntityNotFoundException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//
//@ExtendWith(SpringExtension.class)
//@ContextConfiguration("/testApplicationContext.xml")
//public class UserDaoTest {
//
//    @Autowired
//    private UserDao userDao;
//    @Autowired
//    private TestDataCleaner testDataCleaner;
//
//    @BeforeEach
//    public void setUp() {
//        testDataCleaner.cleanUp();
//    }
//
//    private User setData() {
//        User user = new User("test@test.com", "hashed_password", "test");
//        userDao.add(user);
//
//        return user;
//    }
//
//    @Test
//    public void add_UserAndFindUser() {
//        User user = setData();
//
//        User findUser = userDao.findByEmail(user.email());
//        assertThat(findUser.email()).isEqualTo(user.email());
//        assertThat(findUser.encodedPassword()).isEqualTo(user.encodedPassword());
//        assertThat(user.name()).isEqualTo(findUser.name());
//    }
//
//    @Test
//    public void find_ByEmail_ThrowsException_WhenUserDoesNotExist() {
//        assertThatThrownBy(() -> userDao.findByEmail("unknown")).isInstanceOf(EntityNotFoundException.class);
//    }
//
//    @Test
//    public void exists_ByEmail_ReturnsTrue_WhenUserExists() {
//        User user = setData();
//
//        assertThat(userDao.existsByEmail(user.email())).isTrue();
//    }
//
//    @Test
//    public void exists_ByEmail_ReturnsFalse_WhenUserDoesNotExist() {
//        assertThat(userDao.existsByEmail("unknown")).isFalse();
//    }
//}
