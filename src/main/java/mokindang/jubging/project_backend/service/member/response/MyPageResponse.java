package mokindang.jubging.project_backend.service.member.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class MyPageResponse {

    private final String alias;

    private final String region;

    private final String profileImageUrl;
}