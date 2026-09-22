package domain;

public class User {
    private final String email;
    private final String encodedPassword;
    private final String name;

    //이메일 형식 검증 상수
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    public User(String email, String encodedPassword, String name) {
        validate(email,encodedPassword,name);

        this.email = email;
        this.encodedPassword = encodedPassword;
        this.name = name;
    }

    private void validate(String email, String encodedPassword, String name) {
        if(email==null || !email.matches(EMAIL_REGEX)){
            throw new IllegalArgumentException("유효하지 않은 이메일 형식입니다. "+email);
        }
        if(encodedPassword==null||encodedPassword.isBlank()){
            throw new IllegalArgumentException("비밀번호는 null이거나 비어있을 수 없습니다.");
        }
        if(name==null||name.isBlank()){
            throw new IllegalArgumentException("이름 null이거나 비어있을 수 없습니다.");
        }
    }
    public String getEmail() {
        return this.email;
    }
    public String getEncodedPassword() {
        return encodedPassword;
    }
    public String getName() {
        return name;
    }
}
