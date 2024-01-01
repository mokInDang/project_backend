package mokindang.jubging.project_backend.comment.service.response.commentresponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import mokindang.jubging.project_backend.comment.domain.Comment;
import mokindang.jubging.project_backend.comment.service.response.MultiReplyCommentSelectionResponse;
import mokindang.jubging.project_backend.comment.service.response.ReplyCommentSelectionResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public abstract class AbstractCommentResponse implements CommentResponse {

    @Schema(description = "댓글 id")
    private final Long commentId;

    @Schema(description = "댓글 본문", example = "댓글 본문입니다.")
    private final String commentBody;

    @Schema(description = "댓글 작성 일시")
    private final LocalDateTime createdDatetime;

    @Schema(description = "작성자", example = "작성자닉네임")
    private final String writerAlias;

    @Schema(description = "댓글 수정 여부")
    private final boolean edited;

    @Schema(description = "댓글 작성자의 이메일 앞 4글자")
    private final String firstFourLettersOfEmail;

    @Schema(description = "댓글 작성자의 프로필 경로")
    private final String writerProfileImageUrl;

    @Schema(description = "댓글 조회 회원이, 작성자인지에 대한 정보", allowableValues = {"true", "false"})
    private final boolean mine;

    @Schema(description = "대댓글 목록")
    private final MultiReplyCommentSelectionResponse multiReplyCommentSelectionResponse;

    @Schema(description = "해당 댓글이 게시글의 작성자인지 여부")
    private final boolean writerOfBoard;

    @Override
    public Long getCountOfReplyComment() {
        return multiReplyCommentSelectionResponse.getCountOfReplyComments();
    }

    public AbstractCommentResponse(final Comment comment, final Long memberId, final boolean isWriterOfBoard) {
        this.commentId = comment.getId();
        this.commentBody = comment.getCommentBody()
                .getBody();
        this.createdDatetime = comment.getCreatedDateTime();
        this.writerAlias = comment.getWriterAlias();
        this.edited = comment.wasEdited();
        this.firstFourLettersOfEmail = comment.getFirstFourDigitsOfWriterEmail();
        this.writerProfileImageUrl = comment.getWriterProfileImageUrl();
        this.mine = comment.isSameWriterId(memberId);
        List<ReplyCommentSelectionResponse> replyCommentSelectionResponses = generateReplyCommentSelectionResponses(comment, memberId);
        this.multiReplyCommentSelectionResponse = new MultiReplyCommentSelectionResponse(replyCommentSelectionResponses);
        this.writerOfBoard = isWriterOfBoard;
    }

    private List<ReplyCommentSelectionResponse> generateReplyCommentSelectionResponses(final Comment comment, final Long memberId) {
        return comment.getReplyComments().stream()
                .map(replyComment -> new ReplyCommentSelectionResponse(replyComment, memberId))
                .collect(Collectors.toUnmodifiableList());
    }
}
