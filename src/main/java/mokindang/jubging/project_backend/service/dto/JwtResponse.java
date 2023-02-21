package mokindang.jubging.project_backend.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JwtResponse {

    @JsonIgnore
    private final String accessToken;

    @JsonIgnore
    private final String refreshToken;
}
