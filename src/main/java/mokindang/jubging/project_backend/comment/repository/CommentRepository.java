package mokindang.jubging.project_backend.comment.repository;

import mokindang.jubging.project_backend.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {

    List<Comment> findCommentsByRecruitmentBoardId(final Long boardId);

    List<Comment> findCommentsByCertificationBoardId(final Long boardId);
}
