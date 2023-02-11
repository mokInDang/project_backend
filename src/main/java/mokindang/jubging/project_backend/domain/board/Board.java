package mokindang.jubging.project_backend.domain.board;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mokindang.jubging.project_backend.domain.member.Member;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDate startDate;

    @Enumerated(EnumType.STRING)
    private ActivityCategory activityCategory;

    @Embedded
    private Title title;

    @Embedded
    private Content content;

    public Board(final Member member, final LocalDate startDate, final String activityCategory, final String title, final String content) {
        this.member = member;
        this.startDate = startDate;
        this.activityCategory = ActivityCategory.from(activityCategory);
        this.title = new Title(title);
        this.content = new Content(content);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Board board = (Board) o;

        return Objects.equals(id, board.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
