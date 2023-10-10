package mokindang.jubging.project_backend.recruitment_board.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import mokindang.jubging.project_backend.recruitment_board.service.PaginationCollection;
import mokindang.jubging.project_backend.recruitment_board.service.response.board.SummaryBoardResponse;

import java.util.List;

@Getter
public class MultiBoardPagingResponse {

    @Schema(description = "조회된 요약 게시글 목록")
    private final List<SummaryBoardResponse> boards;

    @Schema(description = "다음 게시글 존재 여부")
    private final boolean hasNext;

    @Schema(description = "다음 커서 게시글 id")
    private final Long nextCursorId;

    public MultiBoardPagingResponse(final PaginationCollection<SummaryBoardResponse> paginationCollection) {
        this.boards = paginationCollection.getPagingItems();
        this.hasNext = paginationCollection.hasNext();
        this.nextCursorId = paginationCollection.getNextCursor().getBoardId();
    }
}
