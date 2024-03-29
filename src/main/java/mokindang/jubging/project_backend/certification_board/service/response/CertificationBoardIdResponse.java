package mokindang.jubging.project_backend.certification_board.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CertificationBoardIdResponse {

    @Schema(description = "게시글 Id", example = "1")
    private Long boardId;
}
