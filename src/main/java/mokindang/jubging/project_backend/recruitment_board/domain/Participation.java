package mokindang.jubging.project_backend.recruitment_board.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import mokindang.jubging.project_backend.member.domain.Member;

import javax.persistence.*;

@IdClass(ParticipationId.class)
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Participation {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_board_id")
    private RecruitmentBoard recruitmentBoard;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public Participation(final RecruitmentBoard recruitmentBoard, final Member writer) {
        this.recruitmentBoard = recruitmentBoard;
        this.member = writer;
    }
}
