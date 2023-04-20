package mokindang.jubging.project_backend.comment.service.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentCreationRequest {

    @Schema(name = "댓글 본문 내용", example = "예시 댓글 입니다.")
    private String commentBody;
}
