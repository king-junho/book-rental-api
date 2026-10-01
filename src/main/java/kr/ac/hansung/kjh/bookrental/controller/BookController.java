package kr.ac.hansung.kjh.bookrental.controller;

import kr.ac.hansung.kjh.bookrental.domain.enums.SearchType;
import kr.ac.hansung.kjh.bookrental.dto.response.BookResponse;
import kr.ac.hansung.kjh.bookrental.dto.response.BookSearchResponse;
import kr.ac.hansung.kjh.bookrental.service.BookService;
import kr.ac.hansung.kjh.bookrental.service.model.BookDetail;
import kr.ac.hansung.kjh.bookrental.service.model.BookSearchResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/search")
    public BookSearchResponse searchBooks(
            @RequestParam SearchType type,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page
    ) {

        BookSearchResult result = bookService.findBookBySearchType(type, keyword, page);

        return BookSearchResponse.from(result);
    }

    @GetMapping("/{bookId}")
    public BookResponse getBook(
            @PathVariable String bookId
    ) {
        BookDetail detail = bookService.findBookDetail(bookId);

        return BookResponse.from(detail);
    }

}
