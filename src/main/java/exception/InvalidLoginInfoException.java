package exception;

public class InvalidLoginInfoException extends RuntimeException{
    public InvalidLoginInfoException() {
        super("아이디나 비밀번호가 일치하지 않습니다.");
    }
    public InvalidLoginInfoException(String msg){
        super(msg);
    }
}
