package kr.ac.hansung.kjh.bookrental.dao;

import kr.ac.hansung.kjh.bookrental.dao.model.BookSearchRow;
import kr.ac.hansung.kjh.bookrental.domain.Book;
import kr.ac.hansung.kjh.bookrental.domain.enums.SearchType;

import java.util.List;

public interface BookDao {
    void deleteAll();

    void add(Book book);

    boolean existsByIsbn(String isbn);

    Book findByIsbn(String isbn);

    void deleteByIsbn(String isbn);

    List<BookSearchRow> findDetailsBySearchType(SearchType searchType, String keyword, int limit, int offset);

    int getCountBySearchType(SearchType searchType, String keyword);
}
