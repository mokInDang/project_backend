package mokindang.jubging.project_backend.domain.board;

import mokindang.jubging.project_backend.domain.board.vo.Title;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class TitleTest {

    @Test
    @DisplayName("제목을 입력 받아 객체를 생성한다.")
    void create() {
        //given
        String title = "제목입니다.";

        //when, then
        assertThatCode(() -> new Title(title)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EmptySource
    @MethodSource("titleValueProvider")
    @DisplayName("게시글의 길이는 1자 이상 140 자 이하여야 한다. 그렇지 않은경우 예외를 발생한다.")
    void validateLength(final String incorrectValue) {
        //when,then
        assertThatThrownBy(() -> new Title(incorrectValue)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("글 제목은 1글자 이상 140자 이하야합니다.");
    }

    private static Stream<Arguments> titleValueProvider() {
        return Stream.of(
                Arguments.of(" "),
                Arguments.of("a".repeat(141))
        );
    }

    @Test
    @DisplayName("제목 내용을 반환한다.")
    void getValue() {
        //given
        String sampleTitle = "테스트용 제목입니다.";
        Title title = new Title(sampleTitle);

        //when
        String actual = title.getValue();

        //then
        assertThat(actual).isEqualTo(sampleTitle);
    }
}
