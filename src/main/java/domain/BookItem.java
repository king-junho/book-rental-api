package domain;

import domain.enums.BookStatus;

public class BookItem {
    private final Long id;
    private final String ISBN;
    private BookStatus status;

    public BookItem(Long id, String ISBN){
        this.id=id;
        this.ISBN=ISBN;
        this.status=BookStatus.AVAILABLE;
    }

    public Long getId(){
        return this.id;
    }
    public String getISBN(){
        return this.ISBN;
    }
    public BookStatus getStatus(){
        return this.status;
    }
    public void setStatus(BookStatus status){
        this.status = status;
    }
}