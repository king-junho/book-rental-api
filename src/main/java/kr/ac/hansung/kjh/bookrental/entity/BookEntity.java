package kr.ac.hansung.kjh.bookrental.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Entity
@Table(name = "books")
@Getter
public class BookEntity {
    @Id
    private String isbn;

    @NotBlank
    private String title;

    @NotBlank
    private String author;

    @NotBlank
    private String genre;
}