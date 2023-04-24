package mokindang.jubging.project_backend.comment.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentIdResponse {

    @Schema(description = "댓글의 id", example = "1")
    private Long CommentId;
}
