package mokindang.jubging.project_backend.domain.board;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;


@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Title {

    private static final int MAXIMUM_TITLE_SIZE = 140;

    private String value;

    public Title(final String value) {
        validateSize(value);
        this.value = value;
    }

    private void validateSize(final String title) {
        if (title.isBlank() || title.length() > MAXIMUM_TITLE_SIZE) {
            throw new IllegalArgumentException("글 제목은 1글자 이상 140자 이하야합니다.");
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Title title = (Title) o;

        return Objects.equals(value, title.value);
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
