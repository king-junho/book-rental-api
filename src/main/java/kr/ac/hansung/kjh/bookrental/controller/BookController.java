package kr.ac.hansung.kjh.bookrental.controller;

import kr.ac.hansung.kjh.bookrental.domain.enums.SearchType;
import kr.ac.hansung.kjh.bookrental.dto.response.BookResponse;
import kr.ac.hansung.kjh.bookrental.entity.BookEntity;
import kr.ac.hansung.kjh.bookrental.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/search")
    public Page<BookEntity> searchBooks(
            @RequestParam SearchType type,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page
    ) {
        return bookService.findBookBySearchType(type, keyword, page);
    }

    @GetMapping("/{isbn}")
    public BookResponse getBook(
            @PathVariable String isbn
    ) {
        return bookService.findBookById(isbn);
    }
}
