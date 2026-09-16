package exception;

public class BookAlreadyRentedException extends RuntimeException {
    public BookAlreadyRentedException() {
        super("이미 대여 중이거나 존재하지 않는 도서입니다.");
    }
    public BookAlreadyRentedException(String message) {
        super(message);
    }
}
