package mokindang.jubging.project_backend.comment.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mokindang.jubging.project_backend.comment.domain.vo.CommentBody;
import mokindang.jubging.project_backend.exception.custom.ForbiddenException;
import mokindang.jubging.project_backend.member.domain.Member;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReplyComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_comment_id", nullable = false)
    Long id;

    @Embedded
    @Column(nullable = false)
    private CommentBody replyCommentBody;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Column(nullable = false)
    private LocalDateTime createdDateTime;

    @Column(nullable = false)
    private LocalDateTime lastModifiedDateTime;

    public ReplyComment(final Comment comment, final String replyCommentBody, final Member writer, final LocalDateTime now) {
        setReplyComment(comment);
        this.replyCommentBody = new CommentBody(replyCommentBody);
        this.writer = writer;
        this.createdDateTime = now;
        this.lastModifiedDateTime = now;
    }

    private void setReplyComment(final Comment comment) {
        this.comment = comment;
        this.comment.addReplyComment(this);
    }


    public void modify(final Long memberId, final String commentBody, final LocalDateTime now) {
        validatePermission(memberId);
        this.replyCommentBody = new CommentBody(commentBody);
        this.lastModifiedDateTime = now;
    }

    public boolean isSameWriterId(final Long memberId) {
        return writer.getId()
                .equals(memberId);
    }

    public void validatePermission(final Long memberId) {
        if (!isSameWriterId(memberId)) {
            throw new ForbiddenException("작성자 권한이 없습니다.");
        }
    }

    public boolean wasEdited() {
        return lastModifiedDateTime.isAfter(createdDateTime);
    }

    public String getWriterProfileImageUrl() {
        return writer.getProfileImage()
                .getProfileImageUrl();
    }

    public String getWriterAlias() {
        return writer.getAlias();
    }

    public String getFirstFourDigitsOfWriterEmail() {
        return writer.getFirstFourDigitsOfWriterEmail();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ReplyComment that = (ReplyComment) o;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
