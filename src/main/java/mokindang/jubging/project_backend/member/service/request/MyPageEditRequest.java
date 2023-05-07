package mokindang.jubging.project_backend.member.service.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MyPageEditRequest {

    @Schema(description = "이미지 첨부파일 Url")
    private final String profileImageUrl;

    @Schema(description = "내정보수정 닉네임")
    private final String alias;
}
