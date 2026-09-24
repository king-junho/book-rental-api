package service;

import dao.BookItemDao;
import domain.BookItem;
import domain.Page;
import domain.PageResult;
import exception.BookAlreadyRentedException;
import exception.BookAlreadyReturnedException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class BookItemService {
    private final BookItemDao bookItemDao;

    public BookItemService(BookItemDao bookItemDao) {
        this.bookItemDao = bookItemDao;
    }

    public BookItem getBookItemById(Long id) {
        return bookItemDao.findById(id);
    }

    public PageResult<BookItem> getBookItemByISBN(String ISBN, int currentPage){
        List<BookItem> data = bookItemDao.findByIsbn(ISBN);
        Page pageInfo = new Page(data.size(),currentPage);

        return new PageResult<>(pageInfo,data);
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

}
