package mokindang.jubging.project_backend.domain.board;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class ContentTest {

    @ParameterizedTest
    @DisplayName("본문 내용을 입력받아 객체를 생성한다.")
    @ValueSource(strings = {"안녕하세요", "1111123", "hello~~~~~"})
    void create(final String input) {
        //when, then
        assertThatCode(() -> new Content(input)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EmptySource
    @MethodSource("contentValueProvider")
    @DisplayName("본문의 길이는 1자 이상 4000자 이하여야 한다. 그렇지 않은경우 예외를 발생한다.")
    void validateLength(final String incorrectValue) {
        //when,then
        assertThatThrownBy(() -> new Title(incorrectValue)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("글 제목은 1글자 이상 140자 이하야합니다.");
    }

    private static Stream<Arguments> contentValueProvider() {
        return Stream.of(
                Arguments.of(" "),
                Arguments.of("a".repeat(4001))
        );
    }

    @Test
    @DisplayName("글 내용을 반환한다.")
    void getValue() {
        //given
        String testContent = "테스트용 샘플 글 내용입니다.";
        Content content = new Content(testContent);

        //when
        String actual = content.getValue();

        //then
        assertThat(actual).isEqualTo(testContent);
    }
}
