package mokindang.jubging.project_backend.recruitment_board.domain.vo;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Latitude {

    private static final Double MIN_LATITUDE = -90.0;
    private static final Double MAX_LATITUDE = 90.0;

    @Column
    private  Double point;

    public Latitude(final Double point) {
        validateRange(point);
        this.point = point;
    }

    private void validateRange(final Double point) {
        if (point < MIN_LATITUDE || point > MAX_LATITUDE) {
            throw new IllegalArgumentException("위도는 -90 부터 90 사이의 값입니다.");
        }
    }

    public Double getPoint() {
        return point;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Latitude latitude = (Latitude) o;

        return Objects.equals(point, latitude.point);
    }

    @Override
    public int hashCode() {
        return point != null ? point.hashCode() : 0;
    }
}
