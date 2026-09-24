package dto.response;

import domain.enums.RentalStatus;

import java.time.LocalDate;

public record RentalResponse(Long rentalId, Long bookItemId, String isbn, LocalDate rentedAt, LocalDate dueDate, LocalDate returnedAt, RentalStatus status) {
}
