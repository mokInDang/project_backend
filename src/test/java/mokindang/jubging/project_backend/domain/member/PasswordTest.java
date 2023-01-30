package mokindang.jubging.project_backend.domain.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class PasswordTest {

    @ParameterizedTest
    @CsvSource(value = {"!qwer123", "cat987654!@#", "*monkey123"})
    @DisplayName("로그인 비밀번호는 영어(소문자), 숫자, 특수문자(!@#$%^&*) 최소 1자 조합으로 20자 이하여야 한다.")
    public void createPassword(final String input){
        //then
        assertThatCode(() -> new Password(input)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @CsvSource(value = {"dog123!", "가나다라마바사아", "Cat123456", "cat123456", "!@#123456", "cat!@#$%^&*", "0123456789012345678901"})
    @DisplayName("로그인 비밀번호는 영어(소문자), 숫자, 특수문자(!@#$%^&*) 최소 1자 조합으로 20자 이하여야 한다. 그렇지 않은 경우 예외를 반환한다. ")
    public void validatePassword(final String input){
        //then
        assertThatThrownBy(() -> new Password(input)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("패스워드는 영어(소문자), 숫자, 특수문자(최소 1자) 조합으로 20자 이하입니다.");

    }

}