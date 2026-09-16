package dao;

import domain.User;

public interface UserDao{
    void deleteAll();
    User findById(String id);
    void add(User user, String hashedPassword);
    boolean isExist(String id);
}
