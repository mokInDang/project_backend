package mokindang.jubging.project_backend.domain.board.recruitment.vo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;
import java.util.Objects;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContentBody {
    private static final int MAXIMUM_CONTENT_SIZE = 4000;

    @Lob
    @Column(name = "content_body", nullable = false)
    private String value;

    public ContentBody(final String value) {
        validateSize(value);
        this.value = value;
    }

    private void validateSize(final String contentBody) {
        if (contentBody.isBlank() || contentBody.length() > MAXIMUM_CONTENT_SIZE) {
            throw new IllegalArgumentException("글 내용은 최소 1자 이상, 최대 4000자 입니다.");
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ContentBody contentBody = (ContentBody) o;

        return Objects.equals(value, contentBody.value);
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
