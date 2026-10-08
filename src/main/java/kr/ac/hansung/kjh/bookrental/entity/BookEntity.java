package kr.ac.hansung.kjh.bookrental.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "books")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookEntity {
    @Id
    private String isbn;

    @NotBlank
    private String title;

    @NotBlank
    private String author;

    @NotBlank
    private String genre;

    public static BookEntity create(String isbn, String title, String author, String genre) {
        BookEntity book = new BookEntity();
        book.isbn = isbn;
        book.title = title;
        book.author = author;
        book.genre = genre;
        return book;
    }
}