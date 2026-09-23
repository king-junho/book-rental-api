package service;

import dao.UserDao;
import domain.User;
import exception.EntityAlreadyExistsException;
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
        if(userDao.existsByEmail(user.getEmail())){
            throw new EntityAlreadyExistsException("이미 존재하는 아이디입니다.");
        }

        String hashedPassword = encoder.encode(user.getEncodedPassword());
        User addUser = new User(user.getEmail(),hashedPassword,user.getName());

        userDao.add(addUser);
    }

    public boolean login(String id, String password){
        User loginUser = userDao.findByEmail(id);

        if(loginUser != null && encoder.matches(password,loginUser.getEncodedPassword())){
            return true;
        }

        throw new InvalidLoginInfoException("아이디나 비밀번호가 일치하지 않습니다.");
    }

    public User getUser(String id){
        return userDao.findByEmail(id);
    }
}
