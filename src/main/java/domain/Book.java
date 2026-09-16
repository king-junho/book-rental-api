package domain;

public class Book {
    private final String ISBN;
    private final String title;
    private final String author;
    private final String genre;

    public Book(String ISBN, String title, String author, String genre) {
        this.ISBN = ISBN;
        this.title = title;
        this.author = author;
        this.genre = genre;
    }
    public String getISBN(){
        return this.ISBN;
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
