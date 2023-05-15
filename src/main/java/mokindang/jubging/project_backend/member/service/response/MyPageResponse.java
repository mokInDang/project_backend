package mokindang.jubging.project_backend.member.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MyPageResponse {

    @Schema(description = "마이페이지 프로필 URL", example = "기본 프로필 이미지 url")
    private final String profileImageUrl;

    @Schema(description = "마이페이지 닉네임", example = "민호")
    private final String alias;

    @Schema(description = "마이페이지 지역명", example = "부천시")
    private final String region;
}
