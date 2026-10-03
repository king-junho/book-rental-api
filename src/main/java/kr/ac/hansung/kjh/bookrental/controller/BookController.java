package kr.ac.hansung.kjh.bookrental.controller;

import kr.ac.hansung.kjh.bookrental.dto.response.BookDetailResponse;
import kr.ac.hansung.kjh.bookrental.dto.response.BookSearchResponse;
import kr.ac.hansung.kjh.bookrental.enums.SearchType;
import kr.ac.hansung.kjh.bookrental.service.BookService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    //다중 책 조회
    @GetMapping("/search")
    public BookSearchResponse searchBooks(@RequestParam SearchType type, @RequestParam String keyword,
                                          @RequestParam(defaultValue = "0") int page) {
        return bookService.findBookBySearchType(type, keyword, page);
    }

    //단일 책 조회
    @GetMapping("/{isbn}")
    public BookDetailResponse getBook(@PathVariable String isbn) {
        return bookService.findBookById(isbn);
    }
}
