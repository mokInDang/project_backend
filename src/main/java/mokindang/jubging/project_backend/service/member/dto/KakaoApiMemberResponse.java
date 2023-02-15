package mokindang.jubging.project_backend.service.member.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KakaoApiMemberResponse {

    private String accessToken;
    private String refreshToken;
    private String alias;
    private String email;
}
