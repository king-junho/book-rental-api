package service;
import dao.BookDao;
import domain.Book;
import service.model.Page;
import domain.enums.SearchType;
import exception.EntityAlreadyExistsException;
import exception.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import service.model.BookDetail;
import service.model.BookSearchResult;

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

    public BookSearchResult findBookBySearchType(SearchType type, String keyword, int currentPage){
        int searchCount = bookDao.getCountBySearchType(type, keyword);
        Page pageInfo = new Page(searchCount,currentPage);
        List<Book> books = bookDao.findBySearchType(type,keyword, pageInfo.getPageSize(),pageInfo.getStartIndex());

        List<BookDetail> bookDetails = books.stream().map(book-> new BookDetail(
                book,
                bookItemService.getBookCount(book.getIsbn()),
                bookItemService.getAvailableBookCount(book.getIsbn())
        )).toList();

        return new BookSearchResult(pageInfo, bookDetails);
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
        if(count<=0){
            throw new IllegalArgumentException("책의 수량은 1권 이상이여야 합니다.");
        }
        if(bookDao.existsByIsbn(book.getIsbn())){
            throw new EntityAlreadyExistsException("이미 등록된 도서입니다. ISBN : "+book.getIsbn());
        }else{
            bookDao.add(book);
            bookItemService.addBookItem(book.getIsbn(),count);
        }
    }
    public BookDetail findBookDetail(String isbn){
        Book book = bookDao.findByIsbn(isbn);

        int totalCount = bookItemService.getBookCount(book.getIsbn());
        int availableCount = bookItemService.getAvailableBookCount(book.getIsbn());

        return new BookDetail(book,totalCount,availableCount);
    }
}

