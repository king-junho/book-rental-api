package kr.ac.hansung.kjh.bookrental.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RentalCreateRequest(
        @NotBlank
        String isbn
) {
}
