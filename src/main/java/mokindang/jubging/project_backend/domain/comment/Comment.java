package mokindang.jubging.project_backend.domain.comment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mokindang.jubging.project_backend.domain.board.recruitment.RecruitmentBoard;
import mokindang.jubging.project_backend.domain.comment.vo.CommentBody;
import mokindang.jubging.project_backend.domain.member.Member;

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
    Long id;

    @Embedded
    @Column(nullable = false)
    private CommentBody commentBody;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member writer;

    @ManyToOne
    @JoinColumn(name = "recruitment_board_id")
    private RecruitmentBoard recruitmentBoard;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.REMOVE)
    private List<ReplyComment> replyComments = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime createdDateTime;

    @Column(nullable = false)
    private LocalDateTime lastModifiedDateTime;

    public Comment(final String commentBody, final Member writer, final LocalDateTime now) {
        this.commentBody = new CommentBody(commentBody);
        this.writer = writer;
        this.createdDateTime = now;
        this.lastModifiedDateTime = createdDateTime;
    }

    public void modify(final String commentBody, final LocalDateTime now) {
        this.commentBody = new CommentBody(commentBody);
        this.lastModifiedDateTime = now;
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
