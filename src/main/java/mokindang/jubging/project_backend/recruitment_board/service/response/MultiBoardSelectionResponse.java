package mokindang.jubging.project_backend.recruitment_board.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class MultiBoardSelectionResponse {

    @Schema(description = "조회된 요약 게시글 목록")
    private final List<SummaryBoardResponse> boards;

    @Schema(description = "다음 게시글 존재 여부")
    private final boolean hasNext;
}
