package mokindang.jubging.project_backend.recruitment_board.domain.vo;

public class Longitude {

    private static final Double MIN_LONGITUDE = -180.0;
    private static final Double MAX_LONGITUDE = 180.0;

    private final Double point;

    public Longitude(final Double point) {
        validateRange(point);
        this.point = point;
    }

    private void validateRange(final Double point) {
        if (point < MIN_LONGITUDE || point > MAX_LONGITUDE) {
            throw new IllegalArgumentException("경도는 -180 부터 180 사이의 값입니다.");
        }
    }

    public Double getPoint() {
        return point;
    }
}
