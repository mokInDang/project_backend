package mokindang.jubging.project_backend.comment.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BoardIdResponse {

    @Schema(description = "게시글 id", example = "1")
    private Long boardId;
}
