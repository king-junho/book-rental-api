package kr.ac.hansung.kjh.bookrental.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class JwtTokenService {

    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final String audience;
    private final Duration accessTokenTtl;

    public JwtTokenService(JwtEncoder jwtEncoder, @Value("${jwt.issuer}") String issuer,
                           @Value("${jwt.audience}") String audience,
                           @Value("${jwt.access-token-ttl}") Duration accessTokenTtl) {
        if (accessTokenTtl.isZero() || accessTokenTtl.isNegative()) {
            throw new IllegalArgumentException("토큰 유효기간은 0보다 커야 합니다.");
        }

        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
        this.audience = audience;
        this.accessTokenTtl = accessTokenTtl;
    }

    public String createToken(String userEmail) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder().issuer(issuer).subject(userEmail).audience(
                List.of(audience)).issuedAt(now).expiresAt(now.plus(accessTokenTtl)).build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}