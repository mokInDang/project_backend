package mokindang.jubging.project_backend.domain.board.certificationboard;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mokindang.jubging.project_backend.domain.board.recruitment.vo.Content;
import mokindang.jubging.project_backend.domain.board.recruitment.vo.Title;
import mokindang.jubging.project_backend.domain.member.Member;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CertificationBoard {

    @Id
    @Column(name = "certification_board_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime createdDateTime;

    @Column(nullable = false)
    private LocalDateTime modifiedTIme;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member writer;

    @Embedded
    private Title title;

    @Embedded
    private Content content;

    public CertificationBoard(final LocalDateTime createdDateTime, final LocalDateTime modifiedTIme,
                              final Member writer, final String title, final String content) {
        this.createdDateTime = createdDateTime;
        this.modifiedTIme = modifiedTIme;
        this.writer = writer;
        this.title = new Title(title);
        this.content = new Content(content);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final CertificationBoard certificationBoard = (CertificationBoard) o;
        return Objects.equals(id, certificationBoard.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
