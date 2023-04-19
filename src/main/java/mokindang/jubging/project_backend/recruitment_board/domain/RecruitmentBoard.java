package mokindang.jubging.project_backend.recruitment_board.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.ContentBody;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.StartingDate;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.Title;
import mokindang.jubging.project_backend.comment.Comment;
import mokindang.jubging.project_backend.member.domain.Member;
import mokindang.jubging.project_backend.member.domain.vo.Region;
import mokindang.jubging.project_backend.exception.custom.ForbiddenException;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitmentBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruitment_board_id", nullable = false)
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
    @Column(name = "content_body")
    private ContentBody contentBody;

    @Embedded
    private Region writingRegion;

    @Column(nullable = false)
    private boolean onRecruitment;

    @OneToMany(mappedBy = "recruitmentBoard", cascade = CascadeType.REMOVE)
    List<Comment> comments = new ArrayList<>();

    public RecruitmentBoard(final LocalDateTime creatingDateTime, final Member writer, final LocalDate startingDate,
                            final String activityCategory, final String title, final String content) {
        this.creatingDateTime = creatingDateTime;
        this.writer = writer;
        LocalDate creatingDate = creatingDateTime.toLocalDate();
        this.startingDate = new StartingDate(creatingDate, startingDate);
        this.activityCategory = ActivityCategory.from(activityCategory);
        this.title = new Title(title);
        this.contentBody = new ContentBody(content);
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

    public void closeRecruitment(final Long memberId) {
        validatePermission(memberId);
        this.onRecruitment = false;
    }

    public void checkRegion(final Region region) {
        if (!this.writingRegion.equals(region)) {
            throw new IllegalArgumentException("지역이 다른 유저는 게시글에 접근 할 수 없습니다.");
        }
    }

    public void modify(final Long memberId, final LocalDate startingDate, final String activityCategoryValue,
                       final String titleValue, final String contentValue) {
        validatePermission(memberId);
        LocalDate today = LocalDate.now();
        this.startingDate = new StartingDate(today, startingDate);
        this.activityCategory = ActivityCategory.from(activityCategoryValue);
        this.title = new Title(titleValue);
        this.contentBody = new ContentBody(contentValue);
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

    public void addComment(final Comment comment) {
        this.comments.add(comment);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RecruitmentBoard recruitmentBoard = (RecruitmentBoard) o;
        return Objects.equals(id, recruitmentBoard.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
