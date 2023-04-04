package mokindang.jubging.project_backend.service.member.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MyPageResponse {

    @Schema(description = "마이페이지 닉네임", example = "민호")
    private final String alias;

    @Schema(description = "마이페이지 지역명", example = "부천시")
    private final String region;

    @Schema(description = "마이페이지 프로필 URL", example = "DEFAULT_PROFILE_IMAGE_URL")
    private final String profileImageUrl;
}