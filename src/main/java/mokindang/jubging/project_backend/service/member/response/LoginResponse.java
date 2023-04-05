package mokindang.jubging.project_backend.service.member.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginResponse {

    private final String email;

    private final String alias;

    private final String region;

    private final String profileImageUrl;
}
