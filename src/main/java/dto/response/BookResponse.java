package dto.response;

import service.model.BookDetail;

public record BookResponse(String isbn, String title, String author, String genre, int totalCount, int availableCount) {
    public static BookResponse from(BookDetail detail){
        return new BookResponse(
            detail.book().getIsbn(),
            detail.book().getTitle(),
            detail.book().getAuthor(),
            detail.book().getGenre(),
            detail.totalCount(),
            detail.availableCount()
        );
    }
}
