package domain;

import domain.enums.RentalStatus;
import java.time.LocalDate;

public class Rental {
    private final Long id;
    private final String userId;
    private final Long bookItemId;
    private final LocalDate rentedAt;
    private final LocalDate dueDate;
    private LocalDate returnedAt;
    private RentalStatus status;

    public Rental(Long id, String userId, Long bookItemId, LocalDate rentedAt, RentalStatus status) {
        this.id = id;
        this.userId = userId;
        this.bookItemId = bookItemId;
        this.rentedAt = rentedAt;
        this.dueDate = rentedAt.plusDays(14);
        this.status = status;
    }

    public Long getId() {
        return this.id;
    }

    public String getUserId() {
        return this.userId;
    }
    public Long getBookItemId() {
        return this.bookItemId;
    }
    public LocalDate getRentedAt() {
        return this.rentedAt;
    }
    public LocalDate getDueDate() {
        return this.dueDate;
    }
    public LocalDate getReturnedAt() {
        return this.returnedAt;
    }
    public void setReturnedAt(LocalDate returnedAt) {
        this.returnedAt = returnedAt;
    }
    public RentalStatus getStatus() {
        return this.status;
    }
}

