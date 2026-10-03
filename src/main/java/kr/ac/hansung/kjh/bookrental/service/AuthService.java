package kr.ac.hansung.kjh.bookrental.service;

import kr.ac.hansung.kjh.bookrental.exception.InvalidLoginInfoException;
import kr.ac.hansung.kjh.bookrental.security.JwtTokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;

    public AuthService(AuthenticationManager authenticationManager, JwtTokenService jwtTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
    }

    public String login(String userEmail, String password) {
        Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(userEmail, password));
        } catch (BadCredentialsException e) {
            throw new InvalidLoginInfoException("아이디나 비밀번호가 일치하지 않습니다.");
        }
        return jwtTokenService.createToken(authentication.getName());
    }
}
