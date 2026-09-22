package domain;

import domain.enums.BookItemStatus;

public class BookItem {
    private final Long id;
    private final String isbn;
    private BookItemStatus status;

    private BookItem(Long id, String isbn, BookItemStatus status) {
        validate(isbn,status);
        this.id=id;
        this.isbn=isbn;
        this.status=status;
    }

    public static BookItem create(String isbn) {
        return new BookItem(null, isbn, BookItemStatus.AVAILABLE);
    }

    public static BookItem restore(Long id, String isbn, BookItemStatus status) {
        if(id==null)
            throw new IllegalArgumentException("id는 Null일 수 없습니다.");
        return new BookItem(id, isbn, status);
    }

    private void validate(String isbn, BookItemStatus status) {
        if(isbn==null || isbn.isBlank()){
            throw new IllegalArgumentException("ISBN은 Null이거나 비어있을 수 없습니다.");
        }
        if(status==null){
            throw new IllegalArgumentException("Status는 Null일 수 없습니다.");
        }
    }

    public Long getId(){return this.id;}
    public String getIsbn(){
        return this.isbn;
    }
    public BookItemStatus getStatus(){
        return this.status;
    }

    public void returnBookItem(){
        if(status == BookItemStatus.RENTED){
            status = BookItemStatus.AVAILABLE;
        }else{
            throw new IllegalStateException("대여 중인 책만 반납할 수 있습니다.");
        }
    }

    public void rentBookItem(){
        if(status == BookItemStatus.AVAILABLE){
            status = BookItemStatus.RENTED;
        }else{
            throw new IllegalStateException("대여 중인 책은 대여할 수 없습니다.");
        }
    }
}