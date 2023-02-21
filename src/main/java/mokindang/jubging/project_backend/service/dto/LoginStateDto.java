package mokindang.jubging.project_backend.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mokindang.jubging.project_backend.domain.member.LoginState;

@Getter
@RequiredArgsConstructor
public class LoginStateDto {

    private final String accessToken;
    private final String refreshToken;
    private final String alias;
    private final LoginState loginState;
}
