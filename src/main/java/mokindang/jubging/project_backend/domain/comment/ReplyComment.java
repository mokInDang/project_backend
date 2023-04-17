package mokindang.jubging.project_backend.domain.comment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mokindang.jubging.project_backend.domain.comment.vo.CommentBody;
import mokindang.jubging.project_backend.domain.member.Member;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReplyComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    Long id;

    @Embedded
    @Column(nullable = false)
    private CommentBody replyCommentBody;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member writer;

    @Column(nullable = false)
    private LocalDateTime createdDateTime;

    @Column(nullable = false)
    private LocalDateTime lastModifiedDateTime;

    public ReplyComment(final String replyCommentBody, final Member writer, final LocalDateTime now) {
        this.replyCommentBody = new CommentBody(replyCommentBody);
        this.writer = writer;
        this.createdDateTime = now;
        this.lastModifiedDateTime = now;
    }

    public void modify(final String commentBody, final LocalDateTime now) {
        this.replyCommentBody = new CommentBody(commentBody);
        this.lastModifiedDateTime = now;
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
