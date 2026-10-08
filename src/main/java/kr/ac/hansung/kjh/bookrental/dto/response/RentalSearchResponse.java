package kr.ac.hansung.kjh.bookrental.dto.response;

import kr.ac.hansung.kjh.bookrental.entity.RentalEntity;
import org.springframework.data.domain.Page;

import java.util.List;

public record RentalSearchResponse(List<RentalResponse> content, int page, int size, long totalElements,
                                   int totalPages) {

    public static RentalSearchResponse from(Page<RentalEntity> result) {
        List<RentalResponse> content = result.getContent().stream().map(RentalResponse::from).toList();
        return new RentalSearchResponse(content, result.getNumber(), result.getSize(), result.getTotalElements(),
                result.getTotalPages());
    }
}
