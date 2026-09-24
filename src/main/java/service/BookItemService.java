package service;

import dao.BookItemDao;
import domain.BookItem;

public class BookItemService {
    private final BookItemDao bookItemDao;

    public BookItemService(BookItemDao bookItemDao) {
        this.bookItemDao = bookItemDao;
    }

    public BookItem getBookItemById(Long id) {
        return bookItemDao.findById(id);
    }

    public void addBookItem(String isbn, int count){
        if(count<=0){
            throw new IllegalArgumentException("입고 수량은 1권 이이어야 합니다.");
        }else if(count==1){
            bookItemDao.add(isbn);
        }
        else{
            bookItemDao.addBatch(isbn, count);
        }
    }

    public void deleteAll(){
        bookItemDao.deleteAll();
    }

    public void deleteBookItemById(Long id){
        bookItemDao.deleteById(id);
    }

    public int getBookCount(String isbn){
        return bookItemDao.getBookCount(isbn);
    }

    public int getAvailableBookCount(String isbn){
        return bookItemDao.getAvailableBookCount(isbn);
    }
}
