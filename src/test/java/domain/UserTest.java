package domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class UserTest {

    @Test
    public void correctIdFormatSignin(){
        User user = new User("test@hansung.ac.kr","1234","test");

        assertThat(user.getId()).isEqualTo("test@hansung.ac.kr");
    }

    @Test
    public void incorrectIdFormatSignin(){

        assertThatThrownBy(()-> {
            new User("test","1234","test");
        }).isInstanceOf(IllegalArgumentException.class);
    }
}
