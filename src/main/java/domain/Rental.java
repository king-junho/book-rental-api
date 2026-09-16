package domain;

import domain.enums.RentalStatus;

import java.time.LocalDate;

public class Rental {
    private final String id;
    private final String userId;
    private final String bookId;
    private final LocalDate rentedAt;
    private final LocalDate dueDate;
    private LocalDate returnedAt;
    private RentalStatus status;

    public Rental(String id, String userId, String bookId, LocalDate rentedAt, RentalStatus status) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.rentedAt = rentedAt;
        this.dueDate = rentedAt.plusDays(14);
        this.status = status;
    }

    public String getId() {
        return this.id;
    }

    public String getUserId() {
        return this.userId;
    }
    public String getBookId() {
        return this.bookId;
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
    public void setStatus(RentalStatus status) {
        this.status = status;
    }
}

