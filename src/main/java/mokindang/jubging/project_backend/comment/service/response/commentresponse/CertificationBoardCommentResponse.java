package mokindang.jubging.project_backend.comment.service.response.commentresponse;

import lombok.Getter;
import mokindang.jubging.project_backend.comment.domain.Comment;

@Getter
public class CertificationBoardCommentResponse extends AbstractCommentResponse {

    public CertificationBoardCommentResponse(Comment comment, Long memberId, boolean isWriterOfBoard) {
        super(comment, memberId, isWriterOfBoard);
    }
}
