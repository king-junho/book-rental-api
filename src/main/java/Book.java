public class Book {
    private final String ISBN;
    private String title;
    private String author;
    private String genre;
    private int totalQuantity;
    private int availableQuantity;

    Book(String ISBN){
        this.ISBN = ISBN;
    }
    Book(String ISBN, String title, String author, String genre, int totalQuantity) {
        this.ISBN = ISBN;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.totalQuantity = totalQuantity;
        this.availableQuantity = totalQuantity;
    }
    public String getISBN(){
        return this.ISBN;
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

    public int getTotalQuantity(){return this.totalQuantity;}
    public void setTotalQuantity(int totalQuantity){
        if(totalQuantity <= 0){
            throw new IllegalArgumentException("책의 재고는 1권 이상이어야 합니다.");
        }
        this.totalQuantity = totalQuantity;
    }

    public int getAvailableQuantity(){
        return this.availableQuantity;
    }
    public void setAvailableQuantity(int availableQuantity){
        if(availableQuantity<0){
            throw new IllegalArgumentException("책의 대여 가능 재고는 0권 이상이어야 합니다.");
        }
        if(availableQuantity>this.totalQuantity){
            throw new IllegalArgumentException("대여 가능 수량이 총 수량을 초과할 수 없습니다.");
        }
        this.availableQuantity = availableQuantity;
    }

    //책 신규 입고
    public void addQuantity(int count){
        if(count<=0){
            throw new IllegalArgumentException("입고 수량은 1권 이상이어야 합니다.");
        }
        this.totalQuantity += count;
        this.availableQuantity += count;
    }

    //도서 대여
    public void rentBook(){
        if(this.availableQuantity<=0){
            throw new IllegalArgumentException("현재 대여 가능한 재고가 없습니다.");
        }

        this.availableQuantity--;
    }

    public void returnBook(){
        if(this.availableQuantity>=totalQuantity){
            throw new IllegalArgumentException("대여 가능 수량이 총 수량을 초과할 수 없습니다.");
        }
        this.availableQuantity++;
    }

    public void removeBook(){
        if(this.totalQuantity<=0 || this.availableQuantity<=0){
            throw new IllegalArgumentException("삭제할 책이 없습니다.");
        }
        
        this.totalQuantity--;
        this.availableQuantity--;
    }


}
