package mokindang.jubging.project_backend.comment.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mokindang.jubging.project_backend.comment.domain.vo.CommentBody;
import mokindang.jubging.project_backend.comment.service.BoardType;
import mokindang.jubging.project_backend.exception.custom.ForbiddenException;
import mokindang.jubging.project_backend.member.domain.Member;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", nullable = false)
    private Long id;

    @Column
    private Long boardId;

    @Embedded
    @Column(nullable = false)
    private CommentBody commentBody;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member writer;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.REMOVE)
    private List<ReplyComment> replyComments = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private BoardType boardType;

    @Column(nullable = false)
    private LocalDateTime createdDateTime;

    @Column(nullable = false)
    private LocalDateTime lastModifiedDateTime;

    public Comment(final Long boardId, final BoardType boardType, final String commentBody, final Member writer, final LocalDateTime now) {
        this.boardId = boardId;
        this.boardType = boardType;
        this.commentBody = new CommentBody(commentBody);
        this.writer = writer;
        this.createdDateTime = now;
        this.lastModifiedDateTime = createdDateTime;
    }

    public int countReplyComment() {
        return replyComments.size();
    }

    public void modify(final Long memberId, final String commentBody, final LocalDateTime now) {
        validatePermission(memberId);
        this.commentBody = new CommentBody(commentBody);
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

    public void addReplyComment(final ReplyComment replyComment) {
        this.replyComments
                .add(replyComment);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Comment comment = (Comment) o;

        return Objects.equals(id, comment.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
