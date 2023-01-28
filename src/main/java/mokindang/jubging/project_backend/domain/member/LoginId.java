package mokindang.jubging.project_backend.domain.member;

import java.util.regex.Pattern;

public class LoginId {

    private static final String USER_ID_REGEX = "^[a-z]+[a-z 0-9]{8,19}$";
    private static final Pattern USER_ID_PATTERN = Pattern.compile(USER_ID_REGEX);

    private final String value;

    public LoginId(final String value) {
        validate(value);
        this.value = value;
    }

    private void validate(final String value) {
        if (!USER_ID_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("아이디는 영어(소문자), 숫자 조합으로 8자 이상 20자 이하 입니다.");
        }
    }
}
