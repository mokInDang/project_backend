package mokindang.jubging.project_backend.authentication.domain.token;

import io.jsonwebtoken.JwtException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RefreshToken {

    @Id
    @Column(name = "refresh_token_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long memberId;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expirationTime;

    public RefreshToken(Long memberId, String token, LocalDateTime expirationTime) {
        this.memberId = memberId;
        this.token = token;
        this.expirationTime = expirationTime;
    }

    public void switchRefreshToken(final String newToken, final LocalDateTime time) {
        validateNewToken(newToken, time);
        token = newToken;
    }

    private void validateNewToken(final String newToken, final LocalDateTime time) {
        if (newToken == null || newToken.isBlank()) {
            throw new IllegalArgumentException("refresh token 이 null 이나 공백으로 입력되었습니다.");
        }

        if (newToken.equals(token)) {
            throw new IllegalArgumentException("생성된 토큰이 기존 토큰과 같습니다.");
        }

        if (time.isAfter(expirationTime)) {
            throw new JwtException("Refresh 토큰이 만료되었습니다.");
        }
    }
}
