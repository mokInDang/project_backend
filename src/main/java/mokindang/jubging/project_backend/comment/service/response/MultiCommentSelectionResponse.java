package mokindang.jubging.project_backend.comment.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MultiCommentSelectionResponse {

    @Schema(description = "댓글 리스트")
    List<CommentSelectionResponse> comments;
}
