package mokindang.jubging.project_backend.recruitment_board.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class LongitudeTest {

    @ParameterizedTest
    @ValueSource(doubles = {-180.0, 180.0, 3.1231232132})
    @DisplayName(" -180 ~ 190 사이의 값을 입력받 Longitude 를 생성한다.")
    void create(final Double input) {
        //when, then
        assertThatCode(() -> new Longitude(input)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(doubles = {-180.00000000001, 180.0000000001})
    @DisplayName("-180 ~ 180 사이의 값을 입력 받지 않은 경우 예외를 반환한다.")
    void validateRange(final Double input) {
        //when, then
        assertThatThrownBy(() -> new Longitude(input)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("경도는 -180 부터 180 사이의 값입니다.");
    }

    @Test
    @DisplayName("경도를 반환한다.")
    void getter() {
        //given
        Longitude longitude = new Longitude(1.1);

        //when
        Double point = longitude.getPoint();

        //then
        assertThat(point).isEqualTo(1.1);
    }
}
