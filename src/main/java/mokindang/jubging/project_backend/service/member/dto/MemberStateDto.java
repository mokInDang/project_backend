package mokindang.jubging.project_backend.service.member.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mokindang.jubging.project_backend.domain.member.MemberState;

@Getter
@RequiredArgsConstructor
public class MemberStateDto {
    private final String accessToken;
    private final String refreshToken;
    private final String alias;
    private final MemberState memberState;
}
