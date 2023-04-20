package mokindang.jubging.project_backend.comment.repository;

import mokindang.jubging.project_backend.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Long> {
}
