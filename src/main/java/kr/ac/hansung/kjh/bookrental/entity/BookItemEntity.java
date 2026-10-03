package kr.ac.hansung.kjh.bookrental.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.ac.hansung.kjh.bookrental.enums.BookItemStatus;
import kr.ac.hansung.kjh.bookrental.exception.BookAlreadyRentedException;
import kr.ac.hansung.kjh.bookrental.exception.BookAlreadyReturnedException;
import lombok.Getter;

@Entity
@Getter
@Table(name = "book_items")
public class BookItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String isbn;

    @NotNull
    @Enumerated(EnumType.STRING)
    private BookItemStatus status;

    @Version
    private Long version;

    public void rent() {
        if (this.status != BookItemStatus.AVAILABLE) {
            throw new BookAlreadyRentedException("이미 대여 된 도서입니다. id: " + this.id);
        }
        this.status = BookItemStatus.RENTED;
    }

    public void returnBook() {
        if (this.status == BookItemStatus.AVAILABLE) {
            throw new BookAlreadyReturnedException("이미 반납 된 도서입니다. id: " + this.id);
        }
        this.status = BookItemStatus.AVAILABLE;
    }
}
