package mokindang.jubging.project_backend.domain.board.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.*;

class StartingDateTest {

    private final LocalDate current = LocalDate.of(2023, 2, 16);

    @ParameterizedTest
    @MethodSource("correctStartingDateProvider")
    @DisplayName("활동 시작일 입력받아 객체를 생성한다. 활동 시작일은 과거의 날짜가 될 수 없다.")
    void create(final LocalDate startingDate) {
        //when, then
        assertThatCode(() -> new StartingDate(current, startingDate)).doesNotThrowAnyException();
    }

    private static Stream<Arguments> correctStartingDateProvider() {
        return Stream.of(
                Arguments.of(LocalDate.of(2023, 2, 16)),
                Arguments.of(LocalDate.of(2023, 2, 17)));
    }

    @Test
    @DisplayName("과거의 활동 시작일 입력 시, 예외를 반환한다.")
    void validateDate() {
        //given
        LocalDate pastStartingDate = LocalDate.of(2023, 2, 15);

        //when,then
        assertThatThrownBy(() -> new StartingDate(current, pastStartingDate)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 지난 날짜는 활동 시작일로 할 수 없습니다.");
    }

    @ParameterizedTest
    @MethodSource("localDateInputsProvider")
    @DisplayName("활동 시작일이 입력받은 시간보다 이전에 있으면 true 를 반환한다. 그렇지 않은경우 false 반환")
    void ifBefore(final LocalDate input, final boolean expect) {
        //given
        StartingDate startingDate = new StartingDate(current, LocalDate.of(2023, 3, 16));

        //when
        boolean actual = startingDate.isBefore(input);

        //then
        assertThat(actual).isEqualTo(expect);
    }

    private static Stream<Arguments> localDateInputsProvider() {
        return Stream.of(Arguments.of(LocalDate.of(2023, 3, 15), false),
                Arguments.of(LocalDate.of(2023, 3, 16), false),
                Arguments.of(LocalDate.of(2023, 3, 17), true)
        );
    }
}
