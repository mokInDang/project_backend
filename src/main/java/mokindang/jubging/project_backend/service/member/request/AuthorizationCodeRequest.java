package mokindang.jubging.project_backend.service.member.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthorizationCodeRequest {

    private String authorizationCode;

    public AuthorizationCodeRequest() {
    }
}
