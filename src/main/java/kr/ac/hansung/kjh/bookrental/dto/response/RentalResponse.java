package kr.ac.hansung.kjh.bookrental.dto.response;

import kr.ac.hansung.kjh.bookrental.entity.RentalEntity;
import kr.ac.hansung.kjh.bookrental.enums.RentalStatus;

import java.time.LocalDate;

public record RentalResponse(Long rentalId, Long bookItemId, LocalDate rentedAt, LocalDate dueDate,
                             LocalDate returnedAt, RentalStatus status) {
    public static RentalResponse from(RentalEntity rentalEntity) {
        return new RentalResponse(rentalEntity.getId(), rentalEntity.getBookItemId(), rentalEntity.getRentedAt(), rentalEntity.getDueDate(), rentalEntity.getReturnedAt(), rentalEntity.getStatus());
    }
}
