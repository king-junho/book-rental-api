package kr.ac.hansung.kjh.bookrental.controller;

import jakarta.validation.Valid;
import kr.ac.hansung.kjh.bookrental.dto.request.SignupRequest;
import kr.ac.hansung.kjh.bookrental.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public void signup(@Valid @RequestBody SignupRequest request) {
        String email = request.email();
        String password = request.password();
        String name = request.name();

        userService.signup(email, password, name);
    }
}
