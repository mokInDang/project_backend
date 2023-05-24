package mokindang.jubging.project_backend.comment.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
public class MultiCommentSelectionResponse {

    @Schema(description = "댓글 리스트")
    private final List<CommentSelectionResponse> comments;

    @Schema(description = "댓글과 대댓글의 총 갯수")
    private final Long countOfCommentAndReplyComment;

    public MultiCommentSelectionResponse(final List<CommentSelectionResponse> comments) {
        this.comments = comments;
        this.countOfCommentAndReplyComment = countReplyCommentAndComment(comments);
    }

    private Long countReplyCommentAndComment(final List<CommentSelectionResponse> comments) {
        return (comments.stream()
                .mapToLong(commentSelectionResponse -> commentSelectionResponse.getMultiReplyCommentSelectionResponse().countOfReplyComments)
                .sum()) + comments.size();
    }
}
