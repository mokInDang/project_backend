package mokindang.jubging.project_backend.domain.board.recruitment;

import java.util.Arrays;

public enum ActivityCategory {

    WALK("산책"),
    RUNNING("달리기");

    private final String value;

    ActivityCategory(final String value) {
        this.value = value;
    }

    public static ActivityCategory from(final String value) {
        return Arrays.stream(values())
                .filter(activityCategory -> value.equals(activityCategory.value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 활동 구분 입니다."));
    }

    public String getValue() {
        return value;
    }
}
