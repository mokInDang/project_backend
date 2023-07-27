package mokindang.jubging.project_backend.recruitment_board.domain.vo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParticipationCount {

    private static final int DEFAULT_COUNT_VALUE = 1;

    @Column(name = "count_of_participation")
    private int count;

    @Column(name = "max_count_of_participation")
    private int max;

    public static ParticipationCount createDefaultParticipationCount(final int max) {
        return new ParticipationCount(max);
    }

    private ParticipationCount(final int max) {
        this.count = DEFAULT_COUNT_VALUE;
        this.max = max;
    }

    public void countUp() {
        validateCountUp();
        count++;
    }

    private void validateCountUp() {
        if (count + 1 >= max) {
            throw new IllegalStateException("참여 인원이 꽉 찼습니다.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParticipationCount that = (ParticipationCount) o;

        if (count != that.count) return false;
        return max == that.max;
    }

    @Override
    public int hashCode() {
        int result = count;
        result = 31 * result + max;
        return result;
    }
}
