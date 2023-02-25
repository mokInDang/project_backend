package mokindang.jubging.project_backend.service.member.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginResponse {

    private String alias;

    public LoginResponse(String alias) {
        this.alias = alias;
    }
}
