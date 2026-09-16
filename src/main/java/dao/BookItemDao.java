package dao;

import domain.BookItem;
import domain.enums.BookStatus;

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
    //책 상태 변경(대여 가능 or 대여 중)
    void updateStatus(Long id, BookStatus status);
    int rent(Long id);
    int returnBook(Long id);
    int getAvailableBookCount(String isbn);
    List<BookItem> findAvailableBooks(String isbn);
}
