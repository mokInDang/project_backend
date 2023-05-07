package mokindang.jubging.project_backend.recruitment_board.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class LatitudeTest {

    @ParameterizedTest
    @ValueSource(doubles = {-90.0, 90.0, 3.1231232132})
    @DisplayName(" -90 ~ 90 사이의 값을 입력받 Latitude 를 생성한다.")
    void create(final Double input) {
        //when, then
        assertThatCode(() -> new Latitude(input)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(doubles = {-90.00000000001, 90.0000000001})
    @DisplayName("-90 ~ 90 사이의 값을 입력 받지 않은 경우 예외를 반환한다.")
    void validateRange(final Double input) {
        //when, then
        assertThatThrownBy(() -> new Latitude(input)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("위도는 -90 부터 90 사이의 값입니다.");
    }

    @Test
    @DisplayName("위도를 반환한다.")
    void getter() {
        //given
        Latitude latitude = new Latitude(1.1);

        //when
        Double point = latitude.getPoint();

        //then
        assertThat(point).isEqualTo(1.1);
    }
}
