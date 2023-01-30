package mokindang.jubging.project_backend.domain.member;

import java.util.regex.Pattern;

public class Password {

    private static final String USER_PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*])[0-9a-z!@#$%^&*]{8,20}$";
    private static final Pattern USER_PASSWORD_PATTERN = Pattern.compile(USER_PASSWORD_REGEX);

    private final String value;

    public Password(final String value) {
        validatePassword(value);
        this.value = value;
    }

    private void validatePassword(final String value) {
        if (!USER_PASSWORD_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("패스워드는 영어(소문자), 숫자, 특수문자(최소 1자) 조합으로 20자 이하입니다.");
        }
    }


}


