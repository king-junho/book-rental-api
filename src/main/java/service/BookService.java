package service;
import dao.BookDao;
import domain.Book;
import domain.Page;
import domain.PageResult;
import domain.enums.SearchType;
import exception.EntityAlreadyExistsException;
import exception.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class BookService {
    private final BookItemService bookItemService;
    private final BookDao bookDao;

    public BookService(BookItemService bookItemService, BookDao bookDao) {
        this.bookItemService = bookItemService;
        this.bookDao = bookDao;
    }

    //domain.Book 전체 삭제(Book_item도 같이 삭제)
    public void deleteAll() {
        bookDao.deleteAll();
    }

    //domain.Book ISBN으로 조회
    public Book findBookById(String id){
        return bookDao.findByIsbn(id);
    }

    public PageResult<Book> findBookBySearchType(SearchType type, String keyword, int currentPage){
        int searchCount = bookDao.getCountBySearchType(type, keyword);
        Page pageInfo = new Page(searchCount,currentPage);
        List<Book> books = bookDao.findBySearchType(type,keyword, pageInfo.getPageSize(),pageInfo.getStartIndex());

        return new PageResult<>(pageInfo,books);
    }

    public void removeBookById(String id){
        if(!bookDao.existsByIsbn(id)){
            throw new EntityNotFoundException("삭제하려는 도서가 존재하지 않습니다. ISBN : "+id);
        }else{
            bookDao.deleteByIsbn(id);
        }
    }

    @Transactional
    public void addBook(Book book, int count){
        if(count<0){
            throw new IllegalArgumentException("책의 수량은 음수값을 가질 수 없습니다.");
        }
        if(bookDao.existsByIsbn(book.getIsbn())){
            throw new EntityAlreadyExistsException("이미 등록된 도서입니다. ISBN : "+book.getIsbn());
        }else{
            bookDao.add(book);
            bookItemService.addBookItem(book.getIsbn(),count);
        }
    }
}

