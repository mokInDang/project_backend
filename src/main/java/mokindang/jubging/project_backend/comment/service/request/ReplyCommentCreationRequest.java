package mokindang.jubging.project_backend.comment.service.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReplyCommentCreationRequest {

    @Schema(description = "대댓글 본문")
    @NotEmpty
    @NotNull
    private String replyCommentBody;
}
