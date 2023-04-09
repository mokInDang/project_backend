package mokindang.jubging.project_backend.domain.comment.vo;

import lombok.Getter;

@Getter
public class CommentBody {

    private static final int MAXIMUM_COMMENT_BODY_SIZE = 1000;

    private final String content;

    public CommentBody(final String content) {
        validateComment(content);
        this.content = content;
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
}
