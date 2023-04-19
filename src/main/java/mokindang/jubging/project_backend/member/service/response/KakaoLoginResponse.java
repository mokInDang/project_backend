package mokindang.jubging.project_backend.member.service.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mokindang.jubging.project_backend.member.domain.LoginState;

@Getter
@RequiredArgsConstructor
public class KakaoLoginResponse {

    @JsonIgnore
    private final String accessToken;

    @JsonIgnore
    private final String refreshToken;

    private final String email;

    private final String alias;

    private final String region;

    private final String profileImageUrl;

    private final LoginState loginState;
}
