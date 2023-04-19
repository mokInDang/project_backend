package mokindang.jubging.project_backend.comment.vo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class CommentBody {

    private static final int MAXIMUM_COMMENT_BODY_SIZE = 1000;

    private String body;

    public CommentBody(final String body) {
        validateComment(body);
        this.body = body;
    }

    private void validateComment(final String commentBody) {
        validateCommentBodyFormat(commentBody);
        validateCommentBodySize(commentBody);
    }

    private void validateCommentBodyFormat(final String commentBody) {
        if (commentBody.isBlank()) {
            throw new IllegalArgumentException("댓글 본문은 공백 제외 1자 이상이어야 합니다.");
        }
    }

    private void validateCommentBodySize(final String commentBody) {
        if (commentBody.length() >= MAXIMUM_COMMENT_BODY_SIZE) {
            throw new IllegalArgumentException("댓글 본문은 1000자 이상이 될 수 없습니다.");
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final CommentBody that = (CommentBody) o;

        return Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return body != null ? body.hashCode() : 0;
    }
}
