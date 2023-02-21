package mokindang.jubging.project_backend.service.member.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginResponseDto {

    @JsonIgnore
    private final String accessToken;

    @JsonIgnore
    private final String refreshToken;

    private final String alias;
}
