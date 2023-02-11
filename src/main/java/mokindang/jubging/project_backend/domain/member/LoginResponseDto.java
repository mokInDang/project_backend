package mokindang.jubging.project_backend.domain.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginResponseDto {

    private final String accessToken;
    private final String refreshToken;
    private final String alias;
}
