package kr.ac.hansung.kjh.bookrental.service;

import kr.ac.hansung.kjh.bookrental.entity.UserEntity;
import kr.ac.hansung.kjh.bookrental.exception.CustomException;
import kr.ac.hansung.kjh.bookrental.exception.ErrorCode;
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

    public void signup(String userEmail, String password, String name) {
        if (userRepository.existsByEmail(userEmail)) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        String encodedPassword = encoder.encode(password);
        UserEntity user = UserEntity.createUser(userEmail, encodedPassword, name);

        userRepository.save(user);
    }
}
