package kr.ac.hansung.kjh.bookrental.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.List;

@Configuration
public class JwtConfig {
    @Bean
    public SecretKey jwtSecretKey(@Value("${jwt.secret}") String base64Secret) {
        byte[] keyBytes = Base64.getDecoder().decode(base64Secret);

        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("HS256 키는 최소 32바이트여야 합니다.");
        }

        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey jwtSecretKey) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtSecretKey));
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey jwtSecretKey, @Value("${jwt.issuer}") String issuer,
                                 @Value("${jwt.audience}") String audience) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(jwtSecretKey).macAlgorithm(
                MacAlgorithm.HS256).build();

        // 기본 시간 검증과 발급자 검증
        var defaultValidator = JwtValidators.createDefaultWithIssuer(issuer);

        // 이 API를 대상으로 발급된 토큰인지 검증
        var audienceValidator = new JwtClaimValidator<List<String>>("aud",
                audiences -> audiences != null && audiences.contains(audience));

        // 우리 액세스 토큰은 만료 시각과 사용자 식별자가 필수
        var expirationRequired = new JwtClaimValidator<Object>("exp", value -> value != null);

        var subjectRequired = new JwtClaimValidator<String>("sub", subject -> subject != null && !subject.isBlank());

        decoder.setJwtValidator(
                new DelegatingOAuth2TokenValidator<Jwt>(defaultValidator, audienceValidator, expirationRequired,
                        subjectRequired));

        return decoder;
    }
}