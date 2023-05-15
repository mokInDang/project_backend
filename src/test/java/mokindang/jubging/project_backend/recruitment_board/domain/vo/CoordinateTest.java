package mokindang.jubging.project_backend.recruitment_board.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

class CoordinateTest {

    @Test
    @DisplayName("좌표 값, 경도 위도를 입력 받아 Coordinate 를 생성한다.")
    void create() {
        //given
        Double x = 1.123456789;
        Double y = 1.123456789;

        //when, then
        assertThatCode(() -> new Coordinate(x, y)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("경도 값을 반환한다.")
    void getLongitude() {
        //given
        Double x = 1.123456789;
        Double y = 2.123456789;

        Coordinate coordinate = new Coordinate(x, y);

        //when
        Double actual = coordinate.getPointOfLongitude();

        //then
        assertThat(actual).isEqualTo(x);
    }

    @Test
    @DisplayName("위도 값을 반환한다.")
    void getLatitude() {
        //given
        Double x = 1.123456789;
        Double y = 2.123456789;
        Coordinate coordinate = new Coordinate(x, y);

        //when
        Double actual = coordinate.getPointOfLatitude();

        //then
        assertThat(actual).isEqualTo(y);
    }
}
