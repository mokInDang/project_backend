package mokindang.jubging.project_backend.service.board.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoardIdResponse {

    @Schema(description = "작성된 게시글 Id", example = "1")
    private Long boardId;
}