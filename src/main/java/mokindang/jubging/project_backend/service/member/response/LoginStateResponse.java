package mokindang.jubging.project_backend.service.member.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mokindang.jubging.project_backend.domain.member.LoginState;

@Getter
@RequiredArgsConstructor
public class LoginStateResponse {

    @JsonIgnore
    private final String accessToken;

    @JsonIgnore
    private final String refreshToken;

    @JsonIgnore
    private final String alias;

    @JsonIgnore
    private final LoginState loginState;
}
