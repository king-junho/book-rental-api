package kr.ac.hansung.kjh.bookrental.domain;

public record Book(String isbn, String title, String author, String genre) {
    public Book {
        validate(isbn, title, author, genre);
    }

    private void validate(String isbn, String title, String author, String genre) {
        if (isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException("ISBN은 Null이거나 비어있을 수 없습니다.");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title은 Null이거나 비어있을 수 없습니다.");
        }
        if (author == null || author.isBlank()) {
            throw new IllegalArgumentException("Author은 Null이거나 비어있을 수 없습니다.");
        }
        if (genre == null || genre.isBlank()) {
            throw new IllegalArgumentException("Genre은 Null이거나 비어있을 수 없습니다.");
        }
    }

}
