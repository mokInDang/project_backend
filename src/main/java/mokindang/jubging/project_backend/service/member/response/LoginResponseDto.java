package mokindang.jubging.project_backend.service.member.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginResponseDto {

    private String alias;

    public LoginResponseDto(String alias) {
        this.alias = alias;
    }
}
