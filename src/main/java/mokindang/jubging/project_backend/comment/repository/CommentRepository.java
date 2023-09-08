package mokindang.jubging.project_backend.comment.repository;

import mokindang.jubging.project_backend.comment.domain.Comment;
import mokindang.jubging.project_backend.comment.service.BoardType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findCommentByBoardTypeAndBoardId(final BoardType boardType, final Long boardId);
}
