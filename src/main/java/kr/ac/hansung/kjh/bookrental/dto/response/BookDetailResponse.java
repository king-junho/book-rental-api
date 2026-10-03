package kr.ac.hansung.kjh.bookrental.dto.response;

import kr.ac.hansung.kjh.bookrental.entity.BookEntity;

public record BookDetailResponse(String isbn, String title, String author, String genre, int totalCount,
                                 int availableCount) {
    public static BookDetailResponse from(BookEntity bookData, int totalCount, int availableCount) {
        return new BookDetailResponse(bookData.getIsbn(), bookData.getTitle(), bookData.getAuthor(),
                bookData.getGenre(), totalCount, availableCount);
    }
}
