package kr.ac.hansung.kjh.bookrental.service;

import kr.ac.hansung.kjh.bookrental.entity.UserEntity;
import kr.ac.hansung.kjh.bookrental.exception.EntityAlreadyExistsException;
import kr.ac.hansung.kjh.bookrental.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;

    public UserService(PasswordEncoder encoder, UserRepository userRepository) {
        this.encoder = encoder;
        this.userRepository = userRepository;
    }

    public void deleteAll() {
        userRepository.deleteAll();
    }

    public void signup(String userEmail, String password, String name) {
        if (userRepository.existsByEmail(userEmail)) {
            throw new EntityAlreadyExistsException("이미 존재하는 아이디입니다.");
        }

        UserEntity data = new UserEntity();
        data.setEmail(userEmail);
        data.setPassword(encoder.encode(password));
        data.setName(name);
        data.setRole("ROLE_USER");

        userRepository.save(data);
    }

    public UserEntity getUser(String userEmail) {
        return userRepository.findByEmail(userEmail);
    }
}
