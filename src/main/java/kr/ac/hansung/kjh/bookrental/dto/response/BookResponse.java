package kr.ac.hansung.kjh.bookrental.dto.response;

import kr.ac.hansung.kjh.bookrental.service.model.BookDetail;

public record BookResponse(String isbn, String title, String author, String genre, int totalCount, int availableCount) {
    public static BookResponse from(BookDetail detail) {
        return new BookResponse(
                detail.book().isbn(),
                detail.book().title(),
                detail.book().author(),
                detail.book().genre(),
                detail.totalCount(),
                detail.availableCount()
        );
    }
}
