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
    private Member member;

    @Embedded
    private StartingDate startingDate;

    @Enumerated(EnumType.STRING)
    private ActivityCategory activityCategory;

    @Embedded
    private Title title;

    @Embedded
    private Content content;

    @Embedded
    private Region writingRegion;

    private boolean onRecruitment;

    public Board(final Member member, final LocalDate startingDate, final String activityCategory, final String title, final String content, final LocalDate now) {
        this.member = member;
        this.startingDate = new StartingDate(now, startingDate);
        this.activityCategory = ActivityCategory.from(activityCategory);
        this.title = new Title(title);
        this.content = new Content(content);
        Region region = member.getRegion();
        validateRegion(region);
        this.writingRegion = region;
        this.onRecruitment = true;
    }

    private void validateRegion(final Region region) {
        if (region.getValue().equals("DEFAULT REGION")) {
            throw new IllegalStateException("지역 인증이 되지 않아, 게시글을 생성할 수 없습니다.");
        }
    }

    public void closeRecruitment() {
        this.onRecruitment = false;
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
