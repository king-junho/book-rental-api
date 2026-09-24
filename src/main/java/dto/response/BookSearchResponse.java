package dto.response;

import service.model.BookSearchResult;

import java.util.List;

public record BookSearchResponse(int currentPage, int totalPages, int totalElements, int pageSize, List<BookResponse> books) {
    public static BookSearchResponse from(BookSearchResult result){
        List<BookResponse> books = result.books().stream().map(BookResponse::from).toList();

        return new BookSearchResponse(
                result.page().getCurrentPage(),
                result.page().getPageCount(),
                result.page().getListCount(),
                result.page().getPageSize(),
                books
        );
    }
}
