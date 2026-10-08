package kr.ac.hansung.kjh.bookrental.service;

import kr.ac.hansung.kjh.bookrental.exception.CustomException;
import kr.ac.hansung.kjh.bookrental.exception.ErrorCode;
import kr.ac.hansung.kjh.bookrental.security.JwtTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenService jwtTokenService;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("로그인 성공하면 인증된 사용자 이름으로 생성한 JWT 토근 반환")
    void login_returnsToken_whenAuthenticationSucceeds() {
        // given
        String email = "test@example.com";
        String password = "test_password";
        String authenticatedEmail = "test@example.com";
        String expectedToken = "issued_access_token";

        Authentication authenticated = UsernamePasswordAuthenticationToken.authenticated(authenticatedEmail, null,
                List.of());

        given(authenticationManager.authenticate(any(Authentication.class))).willReturn(authenticated);
        given(jwtTokenService.createToken(authenticatedEmail)).willReturn(expectedToken);

        // when
        String actualToken = authService.login(email, password);

        // then
        assertThat(actualToken).isEqualTo(expectedToken);

        ArgumentCaptor<Authentication> captor = ArgumentCaptor.forClass(Authentication.class);

        verify(authenticationManager).authenticate(captor.capture());

        Authentication authenticationRequest = captor.getValue();

        assertThat(authenticationRequest).isInstanceOf(UsernamePasswordAuthenticationToken.class);
        assertThat(authenticationRequest.isAuthenticated()).isFalse();
        assertThat(authenticationRequest.getPrincipal()).isEqualTo(email);
        assertThat(authenticationRequest.getCredentials()).isEqualTo(password);

        verify(jwtTokenService).createToken(authenticatedEmail);
    }

    @Test
    @DisplayName("로그인 정보가 일치하지 않으면 예외를 던지고 토큰을 발급하지 않는다")
    void login_throwsCustomException_whenCredentialsInvalid() {
        // given
        String email = "test@example.com";
        String password = "test_password";

        given(authenticationManager.authenticate(any(Authentication.class))).willThrow(
                new BadCredentialsException("인증 실패"));

        // when & then
        assertThatThrownBy(() -> authService.login(email, password)).isInstanceOfSatisfying(CustomException.class,
                exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_CREDENTIALS));

        verifyNoInteractions(jwtTokenService);
    }
}
