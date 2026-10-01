package kr.ac.hansung.kjh.bookrental.dao;

import kr.ac.hansung.kjh.bookrental.domain.User;

public interface UserDao {
    void deleteAll();

    User findByEmail(String email);

    void add(User user);

    boolean existsByEmail(String email);
}
