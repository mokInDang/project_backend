package mokindang.jubging.project_backend.comment.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import mokindang.jubging.project_backend.comment.service.response.commentresponse.CommentResponse;

import java.util.List;

@Getter
public class MultiCommentSelectionResponse {

    @Schema(description = "댓글 리스트")
    private final List<CommentResponse> comments;

    @Schema(description = "댓글과 대댓글의 총 갯수")
    private final Long countOfCommentAndReplyComment;

    @Schema(description = "댓글 작성 가눙 여부")
    private final boolean writingCommentPermission;

    public MultiCommentSelectionResponse(final List<CommentResponse> comments, final boolean writingCommentPermission) {
        this.comments = comments;
        this.countOfCommentAndReplyComment = countReplyCommentAndComment(comments);
        this.writingCommentPermission = writingCommentPermission;
    }

    private Long countReplyCommentAndComment(final List<CommentResponse> comments) {
        return (comments.stream()
                .mapToLong(commentResponse -> commentResponse.getCountOfReplyComment())
                .sum()) + comments.size();
    }
}
