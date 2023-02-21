package mokindang.jubging.project_backend.service.member.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RefreshTokenRequest {

    private String refreshToken;
    public RefreshTokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
