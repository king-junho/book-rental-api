package dao;

import domain.BookItem;
import domain.enums.BookItemStatus;

import java.util.List;

public interface BookItemDao {

    //단일 책 아이템 추가
    void add(String isbn);
    //다중 책 아이템 추가
    void addBatch(String isbn, int count);
    //책 아이템 전체 삭제
    void deleteAll();
    //책 아이템 아이디 기준 단일 삭제
    void deleteById(Long id);
    //책 아이템 아이디 기준 단일 조회
    BookItem findById(Long id);
    //책 ISBN 기준 다중 조회
    List<BookItem> findByIsbn(String isbn);
    int rent(Long id);
    int returnBook(Long id);
    //대여 가능한 권수
    int getAvailableBookCount(String isbn);
    //전체 보유 권수
    int getBookCount(String isbn);
    List<BookItem> findAvailableBooks(String isbn);
}
