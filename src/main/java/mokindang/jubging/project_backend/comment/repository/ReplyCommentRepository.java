package mokindang.jubging.project_backend.comment.repository;

import mokindang.jubging.project_backend.comment.domain.ReplyComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyCommentRepository extends JpaRepository<ReplyComment, Long> {
}
