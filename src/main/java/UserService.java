import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserService {
    private UserDao userDao;
    private BCryptPasswordEncoder encoder;

    public void deleteAll(){
        userDao.deleteAll();
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setEncoder(BCryptPasswordEncoder encoder) {
        this.encoder = encoder;
    }

    public void signup(User user){
        if(userDao.isExist(user.getId())){
            throw new DuplicateKeyException("이미 존재하는 아이디입니다.");
        }

        String hashedPassword = encoder.encode(user.getPassword());
        userDao.add(user,hashedPassword);
    }

    public User login(String id, String password){
        User loginUser = userDao.findById(id);

        if(loginUser != null && encoder.matches(password,loginUser.getPassword())){
            return loginUser;
        }

        throw new IllegalArgumentException("아이디나 비밀번호가 일치하지 않습니다.");
    }
}
