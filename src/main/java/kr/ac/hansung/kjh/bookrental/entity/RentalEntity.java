package kr.ac.hansung.kjh.bookrental.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.ac.hansung.kjh.bookrental.domain.enums.RentalStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "rentals")
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
}