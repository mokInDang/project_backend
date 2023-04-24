package mokindang.jubging.project_backend.comment.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MultiReplyCommentSelectionResponse {

    @Schema(description = "대댓글 리스트")
    List<ReplyCommentSelectionResponse> replyComments;

    @Schema(description = "대댓글의 갯수")
    Long countOfReplyComments;

    public MultiReplyCommentSelectionResponse(final List<ReplyCommentSelectionResponse> replyComments) {
        this.replyComments = replyComments;
        this.countOfReplyComments = (long) replyComments.size();
    }
}
