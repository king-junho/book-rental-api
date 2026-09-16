package dao;

import domain.Book;
import domain.enums.SearchType;

import java.util.List;

public interface BookDao {
    void deleteAll();
    void add(Book book);
    boolean isExist(String id);
    Book findById(String id);
    void deleteById(String isbn);
    List<Book> findBySearchType(SearchType searchType, String keyword, int limit, int offset);
    int getCountBySearchType(SearchType searchType, String keyword);
}
