package kr.ac.hansung.kjh.bookrental.service;

import kr.ac.hansung.kjh.bookrental.dao.BookDao;
import kr.ac.hansung.kjh.bookrental.dao.model.BookSearchRow;
import kr.ac.hansung.kjh.bookrental.domain.Book;
import kr.ac.hansung.kjh.bookrental.domain.enums.SearchType;
import kr.ac.hansung.kjh.bookrental.exception.EntityAlreadyExistsException;
import kr.ac.hansung.kjh.bookrental.exception.EntityNotFoundException;
import kr.ac.hansung.kjh.bookrental.service.model.BookDetail;
import kr.ac.hansung.kjh.bookrental.service.model.BookSearchResult;
import kr.ac.hansung.kjh.bookrental.service.model.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {
    private final BookItemService bookItemService;
    private final BookDao bookDao;

    public BookService(BookItemService bookItemService, BookDao bookDao) {
        this.bookItemService = bookItemService;
        this.bookDao = bookDao;
    }

    //domain.Book 전체 삭제
    public void deleteAll() {
        bookDao.deleteAll();
    }

    //domain.Book ISBN으로 조회
    public Book findBookById(String id) {
        return bookDao.findByIsbn(id);
    }

    public BookSearchResult findBookBySearchType(SearchType type, String keyword, int currentPage) {
        int searchCount = bookDao.getCountBySearchType(type, keyword);
        Page pageInfo = new Page(searchCount, currentPage);
        List<BookSearchRow> rows = bookDao.findDetailsBySearchType(type, keyword, pageInfo.getPageSize(), pageInfo.getStartIndex());

        List<BookDetail> bookDetails = rows.stream().map(row -> new BookDetail(
                row.book(),
                row.totalCount(),
                row.availableCount()
        )).toList();

        return new BookSearchResult(pageInfo, bookDetails);
    }

    public void removeBookById(String id) {
        if (!bookDao.existsByIsbn(id)) {
            throw new EntityNotFoundException("삭제하려는 도서가 존재하지 않습니다. ISBN : " + id);
        } else {
            bookDao.deleteByIsbn(id);
        }
    }

    @Transactional
    public void addBook(Book book, int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("책의 수량은 1권 이상이여야 합니다.");
        }
        if (bookDao.existsByIsbn(book.isbn())) {
            throw new EntityAlreadyExistsException("이미 등록된 도서입니다. ISBN : " + book.isbn());
        } else {
            bookDao.add(book);
            bookItemService.addBookItem(book.isbn(), count);
        }
    }

    public BookDetail findBookDetail(String isbn) {
        Book book = bookDao.findByIsbn(isbn);

        int totalCount = bookItemService.getBookCount(book.isbn());
        int availableCount = bookItemService.getAvailableBookCount(book.isbn());

        return new BookDetail(book, totalCount, availableCount);
    }
}

