package kr.ac.hansung.kjh.bookrental.dto.response;

import kr.ac.hansung.kjh.bookrental.entity.BookEntity;

public record BookResponse(String isbn, String title, String author, String genre) {
    public static BookResponse from(BookEntity book) {
        return new BookResponse(book.getIsbn(), book.getTitle(), book.getAuthor(), book.getGenre());
    }
}
