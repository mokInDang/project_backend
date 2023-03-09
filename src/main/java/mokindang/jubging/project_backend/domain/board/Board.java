package mokindang.jubging.project_backend.domain.board;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mokindang.jubging.project_backend.domain.board.vo.Content;
import mokindang.jubging.project_backend.domain.board.vo.StartingDate;
import mokindang.jubging.project_backend.domain.board.vo.Title;
import mokindang.jubging.project_backend.domain.member.Member;
import mokindang.jubging.project_backend.domain.region.vo.Region;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member writer;

    @Embedded
    private StartingDate startingDate;

    @Enumerated(EnumType.STRING)
    private ActivityCategory activityCategory;

    @Embedded
    private Title title;

    @Embedded
    private Content content;

    @Embedded
    private Region region;

    private boolean onRecruitment;

    public Board(final Member writer, final LocalDate startingDate, final String activityCategory, final String title, final String content, final LocalDate now) {
        this.writer = writer;
        this.startingDate = new StartingDate(now, startingDate);
        this.activityCategory = ActivityCategory.from(activityCategory);
        this.title = new Title(title);
        this.content = new Content(content);
        this.region = writer.getRegion();
        this.onRecruitment = true;
    }

    public String getWriterAlias() {
        return this.writer.getAlias();
    }

    public void closeRecruitment() {
        this.onRecruitment = false;
    }

    public void checkRegion(final Region region) {
        if (!this.region.equals(region)){
            throw new IllegalArgumentException("지역이 다른 유저는 게시글에 접근 할 수 없습니다.");
        }
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
