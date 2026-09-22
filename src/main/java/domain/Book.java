package domain;

public class Book {
    private final String isbn;
    private final String title;
    private final String author;
    private final String genre;

    public Book(String isbn, String title, String author, String genre) {
        validate(isbn, title, author, genre);
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
    }

    private void validate(String isbn, String title, String author, String genre) {
        if(isbn==null || isbn.isBlank()){
            throw new IllegalArgumentException("ISBN은 Null이거나 비어있을 수 없습니다.");
        }
        if(title==null || title.isBlank()){
            throw new IllegalArgumentException("Title은 Null이거나 비어있을 수 없습니다.");
        }
        if(author==null || author.isBlank()){
            throw new IllegalArgumentException("Author은 Null이거나 비어있을 수 없습니다.");
        }
        if(genre==null || genre.isBlank()){
            throw new IllegalArgumentException("Genre은 Null이거나 비어있을 수 없습니다.");
        }
    }
    public String getIsbn(){
        return this.isbn;
    }

    public String getTitle(){
        return this.title;
    }

    public String getAuthor(){
        return this.author;
    }

    public String getGenre(){
        return this.genre;
    }

}
