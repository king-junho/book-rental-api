package kr.ac.hansung.kjh.bookrental.service;

import kr.ac.hansung.kjh.bookrental.dto.response.BookDetailResponse;
import kr.ac.hansung.kjh.bookrental.dto.response.BookSearchResponse;
import kr.ac.hansung.kjh.bookrental.entity.BookEntity;
import kr.ac.hansung.kjh.bookrental.enums.BookItemStatus;
import kr.ac.hansung.kjh.bookrental.enums.SearchType;
import kr.ac.hansung.kjh.bookrental.exception.CustomException;
import kr.ac.hansung.kjh.bookrental.exception.ErrorCode;
import kr.ac.hansung.kjh.bookrental.repository.BookItemRepository;
import kr.ac.hansung.kjh.bookrental.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService {
    private static final int PAGE_SIZE = 5;
    private final BookRepository bookRepository;
    private final BookItemRepository bookItemRepository;

    public BookService(BookRepository bookRepository, BookItemRepository bookItemRepository) {
        this.bookRepository = bookRepository;
        this.bookItemRepository = bookItemRepository;
    }

    @Transactional(readOnly = true)
    public BookDetailResponse findBookById(String isbn) {
        BookEntity bookData = bookRepository.findById(isbn).orElseThrow(
                () -> new CustomException(ErrorCode.BOOK_NOT_FOUND));

        int totalCount = bookItemRepository.countByIsbn(isbn);
        int availableCount = bookItemRepository.countByIsbnAndStatus(isbn, BookItemStatus.AVAILABLE);

        return BookDetailResponse.from(bookData, totalCount, availableCount);
    }

    @Transactional(readOnly = true)
    public BookSearchResponse findBookBySearchType(SearchType type, String keyword, int currentPage) {
        Pageable pageable = PageRequest.of(currentPage, PAGE_SIZE, Sort.by(Sort.Direction.ASC, "isbn"));

        Page<BookEntity> searchData = switch (type) {
            case SearchType.TITLE -> bookRepository.findByTitleContaining(keyword, pageable);
            case SearchType.AUTHOR -> bookRepository.findByAuthorContaining(keyword, pageable);
            case SearchType.GENRE -> bookRepository.findByGenreContaining(keyword, pageable);
        };

        return BookSearchResponse.from(searchData);
    }
}