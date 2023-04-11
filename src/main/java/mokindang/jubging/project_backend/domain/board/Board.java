package mokindang.jubging.project_backend.domain.board;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mokindang.jubging.project_backend.domain.board.vo.Content;
import mokindang.jubging.project_backend.domain.board.vo.StartingDate;
import mokindang.jubging.project_backend.domain.board.vo.Title;
import mokindang.jubging.project_backend.domain.member.Member;
import mokindang.jubging.project_backend.domain.member.vo.Region;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime creatingDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
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
    private Region writingRegion;

    @Column(nullable = false)
    private boolean onRecruitment;

    public Board(final LocalDateTime creatingDateTime, final Member writer, final LocalDate startingDate,
                 final String activityCategory, final String title, final String content) {
        this.creatingDateTime = creatingDateTime;
        this.writer = writer;
        LocalDate creatingDate = creatingDateTime.toLocalDate();
        this.startingDate = new StartingDate(creatingDate, startingDate);
        this.activityCategory = ActivityCategory.from(activityCategory);
        this.title = new Title(title);
        this.content = new Content(content);
        Region region = writer.getRegion();
        validateRegion(region);
        this.writingRegion = region;
        this.onRecruitment = true;
    }

    private void validateRegion(final Region region) {
        if (region.isDefault()) {
            throw new IllegalArgumentException("지역 인증이 되지 않아, 게시글을 생성할 수 없습니다.");
        }
    }

    public void closeRecruitment() {
        this.onRecruitment = false;
    }

    public void checkRegion(final Region region) {
        if (!this.writingRegion.equals(region)) {
            throw new IllegalArgumentException("지역이 다른 유저는 게시글에 접근 할 수 없습니다.");
        }
    }

    public void modify(final LocalDate startingDate, final String activityCategoryValue, final String titleValue, final String contentValue) {
        LocalDate today = LocalDate.now();
        this.startingDate = new StartingDate(today, startingDate);
        this.activityCategory = ActivityCategory.from(activityCategoryValue);
        this.title = new Title(titleValue);
        this.content = new Content(contentValue);
    }

    public boolean isSameWriterId(final Long writerId) {
        return writer.getId()
                .equals(writerId);
    }

    public String getWriterProfileImageUrl() {
        return writer.getProfileImage()
                .getProfileImageUrl();
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
