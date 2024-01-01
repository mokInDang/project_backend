package mokindang.jubging.project_backend.comment.service.response.commentresponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import mokindang.jubging.project_backend.comment.domain.Comment;

@Getter
public class RecruitmentBoardCommentResponse extends AbstractCommentResponse {

    @Schema(description = "댓글의 작성자가 플로깅에 참여한 상태 여부")
    private final boolean writerParticipatedIn;

    public RecruitmentBoardCommentResponse(final Comment comment, final Long memberId, final boolean isWriterOfBoard, final boolean writerParticipatedIn) {
        super(comment, memberId, isWriterOfBoard);
        this.writerParticipatedIn = writerParticipatedIn;
    }
}
