package mokindang.jubging.project_backend.recruitment_board.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mokindang.jubging.project_backend.exception.custom.ForbiddenException;
import mokindang.jubging.project_backend.member.domain.Member;
import mokindang.jubging.project_backend.member.domain.vo.Region;
import mokindang.jubging.project_backend.participation.domain.Participation;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.ContentBody;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.ParticipationCount;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.StartingDate;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.Title;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.place.Place;

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
    private ContentBody contentBody;

    @Embedded
    private Region writingRegion;

    @Column(nullable = false)
    private boolean onRecruitment;

    @Embedded
    private Place meetingPlace;

    @Embedded
    private ParticipationCount participationCount;

    @OneToMany(mappedBy = "recruitmentBoard", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Participation> participationList = new ArrayList<>();

    @Version
    private Long version;

    public RecruitmentBoard(final LocalDateTime creatingDateTime, final Member writer, final LocalDate startingDate,
                            final String activityCategory, final Place meetingPlace, final String title, final String content,
                            final int maxParticipationCount) {
        Region writerRegion = writer.getRegion();
        validateRegion(writerRegion);
        this.creatingDateTime = creatingDateTime;
        this.writer = writer;
        LocalDate creatingDate = creatingDateTime.toLocalDate();
        this.startingDate = new StartingDate(creatingDate, startingDate);
        this.activityCategory = ActivityCategory.from(activityCategory);
        this.title = new Title(title);
        this.contentBody = new ContentBody(content);
        this.writingRegion = writerRegion;
        this.onRecruitment = true;
        this.meetingPlace = meetingPlace;
        this.participationList.add(new Participation(this, writer));
        this.participationCount = ParticipationCount.createDefaultParticipationCount(maxParticipationCount);
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
                       final String titleValue, final String contentValue, final Place place) {
        validatePermission(memberId);
        LocalDate today = LocalDate.now();
        this.startingDate = new StartingDate(today, startingDate);
        this.activityCategory = ActivityCategory.from(activityCategoryValue);
        this.title = new Title(titleValue);
        this.contentBody = new ContentBody(contentValue);
        this.meetingPlace = place;
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

    public boolean isSameRegion(final Region region) {
        return this.writingRegion.equals(region);
    }

    public void addParticipationMember(final Member member) {
        validateAlreadyParticipatingMember(member);
        Participation participation = new Participation(this, member);
        if (onRecruitment) {
            participationCount = participationCount.countUp();
            participationList.add(participation);
            return;
        }
        throw new IllegalArgumentException("모집이 마감된 게시글 입니다.");
    }

    private void validateAlreadyParticipatingMember(final Member member) {
        if (!this.isSameRegion(member.getRegion())) {
            throw new ForbiddenException("타지역 게시글에 참여할 권한이 없습니다.");
        }
        if (isParticipatedIn(member.getId())) {
            throw new IllegalArgumentException("이미 참여가 된 상태 입니다.");
        }
    }

    public boolean isParticipatedIn(final Long memberId) {
        return participationList.stream()
                .anyMatch(participation -> participation.isParticipatedIn(memberId));
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
