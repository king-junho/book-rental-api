public class Book {
    private final String ISBN;
    private String title;
    private String author;
    private String genre;
    private int totalQuantity;
    private int availableQuantity;

    Book(String ISBN, String title, String author, String genre, int totalQuantity) {
        this.ISBN = ISBN;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.totalQuantity = totalQuantity;
        this.availableQuantity = totalQuantity;
    }
    public String getTitle(){
        return this.title;
    }
    public void setTitle(String title){
        this.title = title;
    }

    public String getAuthor(){
        return this.author;
    }
    public void setAuthor(String author){
        this.author = author;
    }

    public String getGenre(){
        return this.genre;
    }
    public void setGenre(String genre){
        this.genre = genre;
    }

    public String getISBN(){
        return this.ISBN;
    }

    public void addTotalQuantity(){
        this.totalQuantity++;
    }
    public int getCount(){
        return this.totalQuantity;
    }

    public void addAvailableQuantity(){
        this.availableQuantity++;
    }

    public void subAvailableQuantity(){
        this.availableQuantity--;
    }
    public int getAvailableQuantity(){
        return this.availableQuantity;
    }


}
