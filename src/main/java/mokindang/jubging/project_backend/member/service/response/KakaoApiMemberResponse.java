package mokindang.jubging.project_backend.member.service.response;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class KakaoApiMemberResponse {

    @JsonIgnore
    private final String email;

    @JsonIgnore
    private final String alias;
}
