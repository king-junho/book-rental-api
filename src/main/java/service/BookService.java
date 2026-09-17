package service;
import dao.BookDao;
import domain.Book;
import domain.Page;
import domain.PageResult;
import domain.enums.SearchType;
import exception.EntityAlreadyExistsException;
import exception.EntityNotFoundException;

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
        Book book = bookDao.findById(id);

        if(book==null){
            throw new EntityNotFoundException("해당 도서를 찾을 수 없습니다. ISBN : "+id);
        }
        return book;
    }

    public PageResult<Book> findBookBySearchType(SearchType type, String keyword, int currentPage){
        int searchCount = bookDao.getCountBySearchType(type, keyword);
        Page pageInfo = new Page(searchCount,currentPage);
        List<Book> books = bookDao.findBySearchType(type,keyword, pageInfo.getPageSize(),pageInfo.getStartIndex());

        return new PageResult<>(pageInfo,books);
    }

    public void removeBookById(String id){
        //book_items들도 다 사라져야 함 (db 자동 삭제) -> 테스트 필요
        if(!bookDao.isExist(id)){
            throw new EntityNotFoundException("삭제하려는 도서가 존재하지 않습니다. ISBN : "+id);
        }else{
            bookDao.deleteById(id);
        }
    }

    public void addBook(Book book, int count){
        if(bookDao.isExist(book.getISBN())){
            throw new EntityAlreadyExistsException("이미 등록된 도서입니다. ISBN : "+book.getISBN());
        }else{
            bookDao.add(book);
            //bookItemService.addBookItems(book,count);
        }
    }
}

