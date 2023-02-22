package mokindang.jubging.project_backend.service.member.response;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class KakaoApiMemberResponse {

    @JsonIgnore
    private final String kakaoAccessToken;

    @JsonIgnore
    private final String kakaoRefreshToken;

    @JsonIgnore
    private final String alias;

    @JsonIgnore
    private final String email;
}
