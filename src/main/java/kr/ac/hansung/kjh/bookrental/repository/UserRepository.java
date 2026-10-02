package kr.ac.hansung.kjh.bookrental.repository;

import kr.ac.hansung.kjh.bookrental.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    boolean existsByEmail(String email);
}