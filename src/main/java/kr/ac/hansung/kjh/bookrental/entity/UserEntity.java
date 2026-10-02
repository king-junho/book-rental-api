package kr.ac.hansung.kjh.bookrental.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity {
    @Id
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String name;

    public static UserEntity createUser(String email, String password, String name) {
        UserEntity user = new UserEntity();
        user.email = email;
        user.password = password;
        user.name = name;
        return user;
    }
}
