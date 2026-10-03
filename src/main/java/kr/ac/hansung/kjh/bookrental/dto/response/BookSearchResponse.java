package kr.ac.hansung.kjh.bookrental.dto.response;

import kr.ac.hansung.kjh.bookrental.entity.BookEntity;
import org.springframework.data.domain.Page;

import java.util.List;

public record BookSearchResponse(List<BookResponse> content, int page, int size, long totalElements, int totalPages) {
    public static BookSearchResponse from(Page<BookEntity> result) {
        List<BookResponse> content = result.getContent().stream().map(BookResponse::from).toList();
        return new BookSearchResponse(content, result.getNumber(), result.getSize(), result.getTotalElements(),
                result.getTotalPages());
    }
}
