package service;

import dao.UserDao;
import domain.User;
import exception.InvalidLoginInfoException;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserService {
    private final UserDao userDao;
    private final PasswordEncoder encoder;

    public UserService(UserDao userDao, PasswordEncoder encoder) {
        this.userDao = userDao;
        this.encoder = encoder;
    }

    public void deleteAll(){
        userDao.deleteAll();
    }

    public void signup(User user){
        if(userDao.isExist(user.getId())){
            throw new UserAlreadyExistsException("이미 존재하는 아이디입니다.");
        }

        String hashedPassword = encoder.encode(user.getPassword());
        userDao.add(user,hashedPassword);
    }

    public boolean login(String id, String password){
        User loginUser = userDao.findById(id);

        if(loginUser != null && encoder.matches(password,loginUser.getPassword())){
            return true;
        }

        throw new InvalidLoginInfoException("아이디나 비밀번호가 일치하지 않습니다.");
    }

    public User getUser(String id){
        return userDao.findById(id);
    }
}
