package controller;

import domain.enums.SearchType;
import dto.response.BookResponse;
import dto.response.BookSearchResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.BookService;
import service.model.BookDetail;
import service.model.BookSearchResult;

@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/search")
    public ResponseEntity<BookSearchResponse> searchBooks(
            @RequestParam SearchType type,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page
            ){

        BookSearchResult result = bookService.findBookBySearchType(type, keyword, page);
        BookSearchResponse response = BookSearchResponse.from(result);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponse> getBook(
            @PathVariable String bookId
    ){
        BookDetail detail = bookService.findBookDetail(bookId);

        BookResponse response = BookResponse.from(detail);

        return ResponseEntity.ok(response);
    }

}
