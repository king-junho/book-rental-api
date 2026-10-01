package kr.ac.hansung.kjh.bookrental.service;

import kr.ac.hansung.kjh.bookrental.domain.enums.BookItemStatus;
import kr.ac.hansung.kjh.bookrental.domain.enums.SearchType;
import kr.ac.hansung.kjh.bookrental.dto.response.BookResponse;
import kr.ac.hansung.kjh.bookrental.entity.BookEntity;
import kr.ac.hansung.kjh.bookrental.exception.EntityNotFoundException;
import kr.ac.hansung.kjh.bookrental.repository.BookItemRepository;
import kr.ac.hansung.kjh.bookrental.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final BookItemRepository bookItemRepository;

    public BookService(BookRepository bookRepository, BookItemRepository bookItemRepository) {
        this.bookRepository = bookRepository;
        this.bookItemRepository = bookItemRepository;
    }

    //domain.Book 전체 삭제
    public void deleteAll() {
        bookRepository.deleteAll();
    }

    //domain.Book ISBN으로 조회
    public BookResponse findBookById(String isbn) {
        BookEntity bookData = bookRepository.findById(isbn).orElse(null);
        int totalCount = bookItemRepository.countByIsbn(isbn);
        int availableCount = bookItemRepository.countByIsbnAndStatus(isbn, BookItemStatus.AVAILABLE);

        return BookResponse.from(bookData, totalCount, availableCount);
    }

    public Page<BookEntity> findBookBySearchType(SearchType type, String keyword, int currentPage) {

        Pageable pageable = PageRequest.of(currentPage, 5);

        Page<BookEntity> result = switch (type) {
            case SearchType.TITLE -> bookRepository.findByTitleContaining(keyword, pageable);
            case SearchType.AUTHOR -> bookRepository.findByAuthorContaining(keyword, pageable);
            case SearchType.GENRE -> bookRepository.findByGenreContaining(keyword, pageable);
        };

        return result;
    }

    @Transactional
    public void removeBookById(String isbn) {
        if (!bookRepository.existsByIsbn(isbn)) {
            throw new EntityNotFoundException("삭제하려는 도서가 존재하지 않습니다. ISBN : " + isbn);
        } else {
            bookRepository.deleteById(isbn);
        }
    }
}