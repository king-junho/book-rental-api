package kr.ac.hansung.kjh.bookrental.exception;

public class RentalAccessDeniedException extends RuntimeException {
    public RentalAccessDeniedException(String message) {
        super(message);
    }
}
