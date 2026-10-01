package kr.ac.hansung.kjh.bookrental.security;

import kr.ac.hansung.kjh.bookrental.entity.UserEntity;
import kr.ac.hansung.kjh.bookrental.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        UserEntity userData = userRepository.findByEmail(email);

        if (userData == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다. " + email);
        }
        return User.withUsername(userData.getEmail())
                .password(userData.getPassword())
                .authorities(userData.getRole())
                .build();
    }

}
