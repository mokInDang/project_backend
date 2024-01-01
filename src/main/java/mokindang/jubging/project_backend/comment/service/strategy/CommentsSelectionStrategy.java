package mokindang.jubging.project_backend.comment.service.strategy;

import mokindang.jubging.project_backend.comment.service.BoardType;
import mokindang.jubging.project_backend.comment.service.response.MultiCommentSelectionResponse;

public interface CommentsSelectionStrategy {

    MultiCommentSelectionResponse selectComments(final Long boardId, final Long memberId);

    BoardType getBoardType();
}
