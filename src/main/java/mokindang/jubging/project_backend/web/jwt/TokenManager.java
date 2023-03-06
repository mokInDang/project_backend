package mokindang.jubging.project_backend.web.jwt;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Duration;
import java.util.Date;

@Component
public class TokenManager {

    private static final String ISSUER = "mokindang";
    private static final String MEMBER_ID = "memberId";
    private static final String TOKEN_TYPE = "Bearer ";
    public static final String DELIMITER = " ";
    private final String secretKey;
    private final Long tokenValidateTime;

    public TokenManager(final @Value("${jwt.secret}") String secretKey,
                        final @Value("${jwt.token-validity-in-seconds}") long tokenValidateTime) {
        this.secretKey = secretKey;
        this.tokenValidateTime = tokenValidateTime * 1000;
    }

    public String createToken(Long memberId) {
        Date now = new Date();
        generateKey();
        String token = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .claim(MEMBER_ID, memberId)
                .setIssuer(ISSUER)
                .setIssuedAt(now)
                .signWith(generateKey(), SignatureAlgorithm.HS256)
                .setExpiration(new Date(now.getTime() + Duration.ofMinutes(tokenValidateTime).toMillis()))
                .compact();
        return TOKEN_TYPE + token;
    }

    public Long getMemberId(String token) {
        String accessToken = parseTokenType(token);
        return Jwts.parserBuilder()
                .setSigningKey(generateKey())
                .build()
                .parseClaimsJws(accessToken)
                .getBody()
                .get(MEMBER_ID,Long.class);
    }

    public boolean validateToken(final String token) {
        try {
            String accessToken = parseTokenType(token);
            Jwts.parserBuilder().setSigningKey(generateKey()).build().parseClaimsJws(accessToken);
            return true;
        } catch (final io.jsonwebtoken.security.SignatureException | SecurityException | MalformedJwtException exception) {
            throw new JwtException("토큰이 유효하지 않습니다.");
        } catch (final ExpiredJwtException exception) {
            throw new JwtException("토큰의 유효기간이 만료되었습니다.");
        } catch (final UnsupportedJwtException exception) {
            throw new JwtException("지원하지 않는 토큰입니다.");
        } catch (final IllegalArgumentException exception) {
            throw new JwtException("JWT 토큰이 잘못되었습니다.");
        } catch (final ArrayIndexOutOfBoundsException exception) {
            throw new JwtException("Token 타입이 명시되지 않았습니다.");
        }
    }

    private String parseTokenType(String token) {
        return token.split(DELIMITER)[1];
    }

    private Key generateKey() {
        return new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS256.getJcaName());
    }
}
