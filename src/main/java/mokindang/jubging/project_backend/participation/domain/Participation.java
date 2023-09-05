package mokindang.jubging.project_backend.participation.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mokindang.jubging.project_backend.member.domain.Member;
import mokindang.jubging.project_backend.recruitment_board.domain.RecruitmentBoard;

import javax.persistence.*;
import java.util.Objects;

@IdClass(ParticipationId.class)
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Participation {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_board_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private RecruitmentBoard recruitmentBoard;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public Participation(final RecruitmentBoard recruitmentBoard, final Member member) {
        this.recruitmentBoard = recruitmentBoard;
        this.member = member;
    }

    public boolean isParticipatedIn(final Long memberId) {
        return this.member
                .getId()
                .equals(memberId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participation that = (Participation) o;
        return Objects.equals(recruitmentBoard.getId(), that.recruitmentBoard.getId()) && Objects.equals(member.getId(), that.member.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(recruitmentBoard, member);
    }
}
