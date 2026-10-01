package kr.ac.hansung.kjh.bookrental.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.ac.hansung.kjh.bookrental.domain.enums.BookItemStatus;
import lombok.Getter;

@Entity
@Getter
@Table(name = "book_items")
public class BookItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String isbn;

    @NotNull
    @Enumerated(EnumType.STRING)
    private BookItemStatus status;
}
