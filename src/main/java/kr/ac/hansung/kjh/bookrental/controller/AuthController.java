package kr.ac.hansung.kjh.bookrental.controller;

import jakarta.validation.Valid;
import kr.ac.hansung.kjh.bookrental.dto.request.LoginRequest;
import kr.ac.hansung.kjh.bookrental.dto.response.TokenResponse;
import kr.ac.hansung.kjh.bookrental.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class AuthController {
    private final AuthService authService;

    public AuthController(
            AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        String accessToken = authService.login(request.email(), request.password());

        return new TokenResponse(accessToken);
    }
}
