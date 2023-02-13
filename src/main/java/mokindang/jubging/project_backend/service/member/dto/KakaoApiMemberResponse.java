package mokindang.jubging.project_backend.service.member.dto;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class KakaoApiMemberResponse {
    private final String accessToken;
    private final String refreshToken;
    private final String alias;
    private final String email;
}
