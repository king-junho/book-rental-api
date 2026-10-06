package kr.ac.hansung.kjh.bookrental.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.ac.hansung.kjh.bookrental.enums.RentalStatus;
import kr.ac.hansung.kjh.bookrental.exception.CustomException;
import kr.ac.hansung.kjh.bookrental.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "rentals")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RentalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String userEmail;

    @NotNull
    private Long bookItemId;

    @NotNull
    private LocalDate rentedAt;

    @NotNull
    private LocalDate dueDate;

    private LocalDate returnedAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RentalStatus status;

    @Version
    private Long version;

    public static RentalEntity create(String userEmail, Long bookItemId) {
        LocalDate now = LocalDate.now();
        RentalEntity rental = new RentalEntity();
        rental.userEmail = userEmail;
        rental.bookItemId = bookItemId;
        rental.rentedAt = now;
        rental.dueDate = now.plusDays(14);
        rental.status = RentalStatus.RENTED;

        return rental;
    }

    public void returnBook(LocalDate returnedDate) {
        if (this.status == RentalStatus.RETURNED || this.returnedAt != null) {
            throw new CustomException(ErrorCode.RENTAL_ALREADY_RETURNED);
        }
        this.returnedAt = returnedDate;
        this.status = RentalStatus.RETURNED;
    }
}