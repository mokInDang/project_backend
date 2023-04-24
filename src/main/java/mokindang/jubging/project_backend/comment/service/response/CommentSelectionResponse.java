package mokindang.jubging.project_backend.comment.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import mokindang.jubging.project_backend.comment.domain.Comment;

import java.time.LocalDateTime;

@Getter
public class CommentSelectionResponse {

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
    MultiReplyCommentSelectionResponse multiReplyCommentSelectionResponse;

    public CommentSelectionResponse(final Comment comment, final Long memberId, final MultiReplyCommentSelectionResponse multiReplyCommentSelectionResponse) {
        this.commentId = comment.getId();
        this.commentBody = comment.getCommentBody()
                .getBody();
        this.createdDatetime = comment.getCreatedDateTime();
        this.writerAlias = comment.getWriterAlias();
        this.edited = comment.wasEdited();
        this.firstFourLettersOfEmail = comment.getFirstFourDigitsOfWriterEmail();
        this.writerProfileImageUrl = comment.getWriterProfileImageUrl();
        this.mine = comment.isSameWriterId(memberId);
        this.multiReplyCommentSelectionResponse = multiReplyCommentSelectionResponse;
    }
}
