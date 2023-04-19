package mokindang.jubging.project_backend.domain.comment.vo;

import mokindang.jubging.project_backend.comment.vo.CommentBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class CommentBodyTest {

    @ParameterizedTest
    @CsvSource(value = {"댓글 본문 예시", "1234123", "❤️ ✅ ⚡️ 🌏", " 안녕하세요!!!@#!@#$"})
    @DisplayName("댓글 본문 값을 입력받아, ContentBody 를 생성한다.")
    void create(final String input) {
        //when, then
        assertThatCode(() -> new CommentBody(input)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {" ", "   "})
    @DisplayName("댓글 본문은 공백 제외 1자 이상이어야 한다. 그렇지 않을 경우 예외를 반환한다.")
    void validateCommentBodyFormat(final String input) {
        //when, then
        assertThatThrownBy(() -> new CommentBody(input)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("댓글 본문은 공백 제외 1자 이상이어야 합니다.");
    }

    @Test
    @DisplayName("댓글 본문의 글자수는 1000자 이상이면 안된다. 그렇지 않을 경우 예외를 반환한다.")
    void validateCommentBodySize() {
        //given
        String input = "a".repeat(1000);

        //when, then
        assertThatThrownBy(() -> new CommentBody(input)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("댓글 본문은 1000자 이상이 될 수 없습니다.");
    }
}
