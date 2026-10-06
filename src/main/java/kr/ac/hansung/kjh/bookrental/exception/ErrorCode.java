package kr.ac.hansung.kjh.bookrental.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    // 400 BAD_REQUEST: 잘못된 요청
    //INVALID_CREDENTIALS(HttpStatus.BAD_REQUEST, "아이디나 비밀번호가 일치하지 않습니다."),

    // 401 UNAUTHORIZED: 잘못된 자격 증명
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "아이디나 비밀번호가 일치하지 않습니다."),

    // 403 FORBIDDEN: 잘못된 리소스 접근 권한
    RENTAL_ACCESS_DENIED(HttpStatus.FORBIDDEN, "본인이 대여한 도서만 반납할 수 있습니다."),

    // 404 NOT_FOUND: 리소스를 찾을 수 없음
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저 정보를 찾을 수 없습니다."), RENTAL_NOT_FOUND(HttpStatus.NOT_FOUND,
            "해당 대여 정보를 찾을 수 없습니다."), BOOKITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 도서를 찾을 수 없습니다."), BOOK_NOT_FOUND(
            HttpStatus.NOT_FOUND, "해당 도서 정보를 찾을 수 없습니다."),

    // 409 CONFLICT: 현재 리소스 상태와 요청 충돌
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 가입된 이메일입니다."), RENTAL_ALREADY_RETURNED(HttpStatus.CONFLICT,
            "이미 반납 처리 된 도서입니다."), RENTAL_ALREADY_RENTED(HttpStatus.CONFLICT,
            "이미 대여 처리 된 도서입니다."), CONCURRENT_MODIFICATION(HttpStatus.CONFLICT,
            "다른 요청에 의해 정보가 변경되었습니다. 다시 시도해 주세요."), BOOK_OUT_OF_STOCK(HttpStatus.CONFLICT, "대여 가능한 책의 재고가 없습니다."),

    // 500 INTERNAL_SERVER_ERROR: 서버 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 에러가 발생했습니다.");

    private final HttpStatus status;
    private final String message;
}
