package dao;

import domain.User;

public interface UserDao{
    void deleteAll();
    User findByEmail(String email);
    void add(User user);
    boolean existsByEmail(String email);
}
