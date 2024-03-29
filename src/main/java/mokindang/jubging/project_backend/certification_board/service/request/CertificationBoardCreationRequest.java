package mokindang.jubging.project_backend.certification_board.service.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class CertificationBoardCreationRequest {

    @NotNull
    @NotBlank
    @Schema(description = "인증 게시글 제목", example = "예시 제목 입니다.")
    private final String title;

    @NotNull
    @NotBlank
    @Schema(description = "인증 게시글 내용", example = "예시 내용 입니다.")
    private final String contentBody;

    private final List<String> fileUrls;
}
