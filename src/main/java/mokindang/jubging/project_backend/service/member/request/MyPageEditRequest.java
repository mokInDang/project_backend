package mokindang.jubging.project_backend.service.member.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@RequiredArgsConstructor
public class MyPageEditRequest {

    @Schema(description = "이미지 첨부파일")
    private final MultipartFile profileImage;

    @Schema(description = "내정보수정 닉네임")
    private final String alias;
}
