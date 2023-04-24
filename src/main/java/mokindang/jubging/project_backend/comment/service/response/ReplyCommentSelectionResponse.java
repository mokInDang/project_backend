package mokindang.jubging.project_backend.comment.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import mokindang.jubging.project_backend.comment.domain.ReplyComment;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReplyCommentSelectionResponse {

    @Schema(description = "대댓글 id")
    private final Long replyCommentId;

    @Schema(description = "대댓글 본문", example = "댓글 본문입니다.")
    private final String replyCommentBody;

    @Schema(description = "대댓글 작성 일시")
    private final LocalDateTime createdDatetime;

    @Schema(description = "작성자", example = "작성자닉네임")
    private final String writerAlias;

    @Schema(description = "대댓글 수정 여부")
    private final boolean edited;

    @Schema(description = "대댓글 작성자의 이메일 앞 4글자")
    private final String firstFourLettersOfEmail;

    @Schema(description = "대댓글 작성자의 프로필 경로")
    private final String writerProfileImageUrl;

    @Schema(description = "대댓글 조회 회원이, 작성자인지에 대한 정보", allowableValues = {"true", "false"})
    private final boolean mine;

    public ReplyCommentSelectionResponse(final ReplyComment replyComment, final Long memberId) {
        this.replyCommentId = replyComment.getId();
        this.replyCommentBody = replyComment.getReplyCommentBody()
                .getBody();
        this.createdDatetime = replyComment.getCreatedDateTime();
        this.writerAlias = replyComment.getWriterAlias();
        this.edited = replyComment.wasEdited();
        this.firstFourLettersOfEmail = replyComment.getFirstFourDigitsOfWriterEmail();
        this.writerProfileImageUrl = replyComment.getWriterProfileImageUrl();
        this.mine = replyComment.isSameWriterId(memberId);
    }
}
