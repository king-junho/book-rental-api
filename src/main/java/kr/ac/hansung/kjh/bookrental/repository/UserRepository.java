package kr.ac.hansung.kjh.bookrental.repository;

import kr.ac.hansung.kjh.bookrental.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    public void deleteAll();

    public boolean existsByEmail(String email);

    public UserEntity findByEmail(String email);
}


//    }
//
//    public User getUser(String userEmail) {
//        return userRepository.findByEmail(userEmail);
//    }
//}
