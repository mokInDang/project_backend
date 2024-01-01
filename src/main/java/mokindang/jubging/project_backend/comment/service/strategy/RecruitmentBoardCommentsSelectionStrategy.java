package mokindang.jubging.project_backend.comment.service.strategy;

import lombok.RequiredArgsConstructor;
import mokindang.jubging.project_backend.comment.domain.Comment;
import mokindang.jubging.project_backend.comment.repository.CommentRepository;
import mokindang.jubging.project_backend.comment.service.BoardType;
import mokindang.jubging.project_backend.comment.service.response.MultiCommentSelectionResponse;
import mokindang.jubging.project_backend.comment.service.response.commentresponse.CommentResponse;
import mokindang.jubging.project_backend.comment.service.response.commentresponse.RecruitmentBoardCommentResponse;
import mokindang.jubging.project_backend.member.domain.Member;
import mokindang.jubging.project_backend.member.service.MemberService;
import mokindang.jubging.project_backend.recruitment_board.domain.RecruitmentBoard;
import mokindang.jubging.project_backend.recruitment_board.service.RecruitmentBoardService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RecruitmentBoardCommentsSelectionStrategy implements CommentsSelectionStrategy {

    private static final BoardType BOARD_TYPE = BoardType.RECRUITMENT_BOARD;

    private final CommentRepository commentRepository;
    private final MemberService memberService;
    private final RecruitmentBoardService recruitmentBoardService;

    @Override
    public MultiCommentSelectionResponse selectComments(final Long boardId, final Long memberId) {
        List<Comment> commentsByRecruitmentBoard = commentRepository.findCommentByBoardTypeAndBoardId(BOARD_TYPE, boardId);
        RecruitmentBoard board = recruitmentBoardService.findByIdWithOptimisticLock(boardId);
        Member member = memberService.findByMemberId(memberId);
        boolean writingCommentPermission = board.isSameRegion(member.getRegion());
        return new MultiCommentSelectionResponse(convertToRecruitmentBoardCommentSelectionResponse(memberId, commentsByRecruitmentBoard, board), writingCommentPermission);
    }

    private List<CommentResponse> convertToRecruitmentBoardCommentSelectionResponse(final Long memberId, final List<Comment> commentsByRecruitmentBoard, final RecruitmentBoard board) {
        return commentsByRecruitmentBoard.stream()
                .map(comment -> {
                    boolean isSameWriterId = board.isSameWriterId(comment.getWriter().getId());
                    boolean isCommentWriterParticipatedIn = board.isParticipatedIn(comment.getWriter().getId());
                    return new RecruitmentBoardCommentResponse(comment, memberId, isSameWriterId, isCommentWriterParticipatedIn);
                })
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public BoardType getBoardType() {
        return BOARD_TYPE;
    }
}
