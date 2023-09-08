package mokindang.jubging.project_backend.comment.service.strategy;

import lombok.RequiredArgsConstructor;
import mokindang.jubging.project_backend.certification_board.domain.CertificationBoard;
import mokindang.jubging.project_backend.certification_board.service.CertificationBoardService;
import mokindang.jubging.project_backend.comment.domain.Comment;
import mokindang.jubging.project_backend.comment.repository.CommentRepository;
import mokindang.jubging.project_backend.comment.service.BoardType;
import mokindang.jubging.project_backend.comment.service.response.CommentSelectionResponse;
import mokindang.jubging.project_backend.comment.service.response.MultiCommentSelectionResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CertificationBoardCommentsSelectionStrategy implements CommentsSelectionStrategy {

    private static final BoardType BOARD_TYPE = BoardType.CERTIFICATION_BOARD;
    private final CommentRepository commentRepository;
    private final CertificationBoardService certificationBoardService;

    @Override
    public MultiCommentSelectionResponse selectComments(Long boardId, Long memberId) {
        List<Comment> commentsByCertificationBoard = commentRepository.findCommentByBoardTypeAndBoardId(BOARD_TYPE, boardId);
        CertificationBoard board = certificationBoardService.findById(boardId);
        return new MultiCommentSelectionResponse(convertToCertificationBoardCommentSelectionResponse2(memberId, commentsByCertificationBoard, board),
                true);
    }

    private List<CommentSelectionResponse> convertToCertificationBoardCommentSelectionResponse2(final Long memberId, final List<Comment> commentsByRecruitmentBoard, final CertificationBoard board) {
        return commentsByRecruitmentBoard.stream()
                .map(comment -> new CommentSelectionResponse(comment, memberId, board.isSameWriterId(comment.getWriter().getId()), false))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public BoardType getBoardType() {
        return BOARD_TYPE;
    }
}
