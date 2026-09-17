package service;

import dao.BookItemDao;
import domain.BookItem;
import domain.Page;
import domain.PageResult;
import exception.BookAlreadyRentedException;
import exception.BookAlreadyReturnedException;

import java.util.List;

public class BookItemService {
    private final BookItemDao bookItemDao;
    private final RentalService rentalService;

    public BookItemService(BookItemDao bookItemDao, RentalService rentalService) {
        this.bookItemDao = bookItemDao;
        this.rentalService = rentalService;
    }

    public BookItem getBookItemById(Long id) {
        return bookItemDao.findById(id);
    }

    public PageResult<BookItem> getBookItemByISBN(String ISBN, int currentPage){
        List<BookItem> data = bookItemDao.findByIsbn(ISBN);
        Page pageInfo = new Page(data.size(),currentPage);

        return new PageResult<>(pageInfo,data);
    }

    public void addBookItem(String ISBN, int count){
        if(count<=0){
            throw new IllegalArgumentException("입고 수량은 1권 이이어야 합니다.");
        }else if(count==1){
            bookItemDao.add(ISBN);
        }
        else{
            bookItemDao.addBatch(ISBN, count);
        }
    }

    public void deleteAll(){
        bookItemDao.deleteAll();
    }

    public void deleteBookItemById(Long id){
        bookItemDao.deleteById(id);
    }

    public void rentBookItem(Long bookId, String userId){
        int updateRowCnt = bookItemDao.rent(bookId);

        if(updateRowCnt==0){
            //책이 존재하지 않으면 조회하며 예외 처리
            BookItem item = bookItemDao.findById(bookId);
            //책이 존재하지만 update가 안 된 경우는 이미 빌리고 있는 책일 경우
            throw new BookAlreadyRentedException("이미 대여 중인 책입니다. id : "+bookId);
        }

        //RentalService 호출해서 Rental 기록남기기
        rentalService.recoredRentalHistory(bookId,userId);
    }

    public void returnBookItem(Long id){
        int updateRowCnt = bookItemDao.returnBook(id);

        if(updateRowCnt==0){
            //책이 존재하지 않으면 조회하며 에러 처리
            BookItem item = bookItemDao.findById(id);
            throw new BookAlreadyReturnedException("이미 반납 된 도서입니다. id : "+id);
        }
        //RentalService호출해서 기존 정보 수정하기
        rentalService.recoredReturnHistory(id);
    }



}
