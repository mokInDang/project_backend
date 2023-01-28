package mokindang.jubging.project_backend.domain.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class LoginIdTest {

    @ParameterizedTest
    @CsvSource(value = {"apple123", "mouse123"})
    @DisplayName("로그인 아이디는 영어(소문자), 숫자 조합으로 8자 이상 20자 이하여야한다.")
    void create(final String input) {
        //then
        assertThatCode(() -> new LoginId(input)).doesNotThrowAnyException();
    }


    @ParameterizedTest
    @CsvSource(value = {"apple12", "가나다라마바사아", "Apple123", "1234567890123456789ab", "!!apple123"})
    @DisplayName("로그인 아이디는 영어(소문자), 숫자 조합으로 8자 이상 20자 이하여야한다. 그렇지 않은 경우 예외를 반환한다.")
    void validate(final String input) {
        //then
        assertThatThrownBy(() -> new LoginId(input)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("아이디는 영어(소문자), 숫자 조합으로 8자 이상 20자 이하 입니다.");
    }
}
