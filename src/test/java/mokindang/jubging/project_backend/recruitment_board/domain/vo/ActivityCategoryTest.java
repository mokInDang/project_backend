package mokindang.jubging.project_backend.recruitment_board.domain.vo;

import mokindang.jubging.project_backend.recruitment_board.domain.ActivityCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class ActivityCategoryTest {

    @ParameterizedTest
    @MethodSource("activityCategoryProvider")
    @DisplayName("입력 받은 활동 구분에 맞는 객체를 반환한다.")
    void from(final String input, final ActivityCategory expect) {
        //when
        ActivityCategory actual = ActivityCategory.from(input);

        //then
        assertThat(actual).isEqualTo(expect);
    }

    private static Stream<Arguments> activityCategoryProvider() {
        return Stream.of(
                Arguments.of("산책", ActivityCategory.WALK),
                Arguments.of("달리기", ActivityCategory.RUNNING)
        );
    }

    @Test
    @DisplayName("존재하지 않는 활동 구분이 입력되면 예외를 반환한다.")
    void fromFailedByNonexistentValue() {
        //given
        String incorrectInput = "누워있기";

        //when, then
        assertThatThrownBy(() -> ActivityCategory.from(incorrectInput)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 활동 구분 입니다.");
    }
}
