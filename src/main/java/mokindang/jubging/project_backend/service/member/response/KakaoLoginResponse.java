package mokindang.jubging.project_backend.service.member.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mokindang.jubging.project_backend.domain.member.LoginState;

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

    private final LoginState loginState;
}
