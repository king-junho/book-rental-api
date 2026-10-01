package kr.ac.hansung.kjh.bookrental.dto.response;

import kr.ac.hansung.kjh.bookrental.domain.Rental;
import kr.ac.hansung.kjh.bookrental.domain.enums.RentalStatus;

import java.time.LocalDate;

public record RentalResponse(Long rentalId, Long bookItemId, LocalDate rentedAt, LocalDate dueDate,
                             LocalDate returnedAt, RentalStatus status) {
    public static RentalResponse from(Rental rental) {
        return new RentalResponse(rental.getId(), rental.getBookItemId(), rental.getRentedAt(), rental.getDueDate(), rental.getReturnedAt(), rental.getStatus());
    }
}
