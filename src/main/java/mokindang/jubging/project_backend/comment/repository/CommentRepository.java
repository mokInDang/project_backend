package mokindang.jubging.project_backend.comment.repository;

import mokindang.jubging.project_backend.comment.domain.Comment;
import mokindang.jubging.project_backend.comment.repository.response.CommentCountResponse;
import mokindang.jubging.project_backend.comment.service.BoardType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findCommentByBoardTypeAndBoardId(final BoardType boardType, final Long boardId);

    @Query("SELECT COUNT(DISTINCT c.id) + COUNT(rc.id) AS commentCount " +
            "FROM Comment c " +
            "LEFT JOIN c.replyComments rc ON c.id = rc.comment.id " +
            "where c.boardId IN :boardIds AND c.boardType =:boardType " +
            "GROUP BY c.boardId")
    List<CommentCountResponse> countALLCommentByBoardIds(final List<Long> boardIds, final BoardType boardType);
}
