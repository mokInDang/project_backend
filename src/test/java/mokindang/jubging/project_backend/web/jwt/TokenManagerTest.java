package mokindang.jubging.project_backend.web.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.spec.SecretKeySpec;

import java.security.Key;
import java.time.Duration;
import java.util.Date;

import static org.assertj.core.api.Assertions.*;

class TokenManagerTest {

    private final String SECRET_KEY = "91bewgjjrt491asf1a2qf6asavhhw981aadgh0qgeg6se8eq8svaxb1xfnd14h651swkkfsv1651aflkasnif01qwagg35asvsdhuygfhg132afa";
    private static final long TOKEN_VALIDITY_IN_SECONDS = 1800L;
    private final Long memberId = 3L;
    private final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;

    private final TokenManager tokenManager = new TokenManager(SECRET_KEY, TOKEN_VALIDITY_IN_SECONDS);

    private Key generateKey(final String secretKey) {
        byte[] keyBytes = secretKey.getBytes();
        return new SecretKeySpec(keyBytes, SIGNATURE_ALGORITHM.getJcaName());
    }


    @Test
    @DisplayName("memberId가 주어지면 토큰을 생성한다.")
    void createToken() {
        //when
        String token = tokenManager.createToken(memberId);

        //then
        assertThat(token).isNotEmpty();
    }

    @Test
    @DisplayName("토큰이 가지고 있는 memberId를 반환할 수 있다.")
    void getEmail() {
        //given
        String token = tokenManager.createToken(memberId);

        //when
        Long findMemberId = tokenManager.getMemberId(token);

        //then
        assertThat(findMemberId).isEqualTo(memberId);
    }

    @Test
    @DisplayName("토큰이 유효한지 검사를 진행하고 문제가 없을시 true를 반환한다.")
    void validateToken() {
        //given
        String token = tokenManager.createToken(memberId);

        //when
        boolean validation = tokenManager.validateToken(token);

        //then
        assertThat(validation).isTrue();
    }


    @Test
    @DisplayName("Token의 signature가 유효한지 검사한다. 유효하지 않을 경우 예외 반환")
    void validateTokenWhenSignatureException() {
        //given
        String invalidKey = "asf65asf16s5a651q9e1vg8a6geag51sg65s1g6d1g61g";
        String invalidToken = Jwts.builder()
                .claim("memberId", memberId)
                .signWith(generateKey(invalidKey), SignatureAlgorithm.HS256)
                .setExpiration(new Date(System.currentTimeMillis() + Duration.ofSeconds(10).toMillis()))
                .compact();

        //when, then
        assertThatThrownBy(() -> tokenManager.validateToken(invalidToken)).isInstanceOf(JwtException.class)
                .hasMessage("토큰이 유효하지 않습니다.");
    }

    @Test
    @DisplayName("Token의 구조를 검사한다. 구조가 잘못된 경우 예외 반환")
    void validateTokenWhenMalformedJwtException() {
        //given
        String token = "extoken";

        //when, then
        assertThatThrownBy(() -> tokenManager.validateToken(token)).isInstanceOf(JwtException.class)
                .hasMessage("토큰이 유효하지 않습니다.");
    }

    @Test
    @DisplayName("Token의 기한이 만료되었는지 검사한다. 만료되었을 경우 예외 반환")
    void validateTokenWhenExpiredJwtException() {
        //given
        String token = Jwts.builder()
                .claim("memberId", memberId)
                .signWith(generateKey(SECRET_KEY), SignatureAlgorithm.HS256)
                .setExpiration(new Date(System.currentTimeMillis() - Duration.ofSeconds(10).toMillis()))
                .compact();

        //when, then
        assertThatThrownBy(() -> tokenManager.validateToken(token)).isInstanceOf(JwtException.class)
                .hasMessage("토큰의 유효기간이 만료되었습니다.");
    }

    @Test
    @DisplayName("Token의 signature가 존재하는지 검사한다. 없을 경우 예외 반환")
    void validateTokenWhenUnsupportedJwtException() {
        //given
        String token = Jwts.builder()
                .claim("memberId", memberId)
                .setExpiration(new Date(System.currentTimeMillis() + Duration.ofSeconds(10).toMillis()))
                .compact();

        //when, then
        assertThatThrownBy(() -> tokenManager.validateToken(token)).isInstanceOf(JwtException.class)
                .hasMessage("지원하지 않는 토큰입니다.");
    }


}