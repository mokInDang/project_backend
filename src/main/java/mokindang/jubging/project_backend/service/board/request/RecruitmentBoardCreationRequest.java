package mokindang.jubging.project_backend.service.board.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Schema(description = "BoardCreateRequest")
@Getter
@RequiredArgsConstructor
public class RecruitmentBoardCreationRequest {

    @NotNull
    @NotBlank
    @Schema(description = "게시글 제목", example = "예시 제목 입니다.")
    private final String title;

    @NotNull
    @NotBlank
    @Schema(description = "게시글 본문", example = "예시 본문 입니다.")
    private final String contentBody;

    @NotNull
    @NotBlank
    @Schema(description = "활동 종류", example = "달리기", allowableValues = {"달리기", "산책"})
    private final String activityCategory;

    @NotNull
    @Schema(description = "활동 시작일", example = "2023-11-23", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private final LocalDate startingDate;
}
