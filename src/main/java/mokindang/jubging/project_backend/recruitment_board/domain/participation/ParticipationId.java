package mokindang.jubging.project_backend.recruitment_board.domain.participation;

import java.io.Serializable;
import java.util.Objects;

public class ParticipationId implements Serializable {

    private Long recruitmentBoard;
    private Long member;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParticipationId that = (ParticipationId) o;

        if (!Objects.equals(recruitmentBoard, that.recruitmentBoard))
            return false;
        return Objects.equals(member, that.member);
    }

    @Override
    public int hashCode() {
        int result = recruitmentBoard != null ? recruitmentBoard.hashCode() : 0;
        result = 31 * result + (member != null ? member.hashCode() : 0);
        return result;
    }
}
