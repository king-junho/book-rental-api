package kr.ac.hansung.kjh.bookrental.service;

import kr.ac.hansung.kjh.bookrental.security.JwtTokenService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationManager;

public class AuthServiceUnitTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenService jwtTokenService;
    @InjectMocks
    private AuthService authService;
}
