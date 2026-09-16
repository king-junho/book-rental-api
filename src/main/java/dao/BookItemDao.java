package dao;

import domain.Book;

public interface BookItemDao {
    void add(Book book);
    void remove(String id);
    int rent(Long id);
}
