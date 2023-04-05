package mokindang.jubging.project_backend.service.board;

import lombok.RequiredArgsConstructor;
import mokindang.jubging.project_backend.domain.board.Board;
import mokindang.jubging.project_backend.domain.member.Member;
import mokindang.jubging.project_backend.exception.custom.ForbiddenException;
import mokindang.jubging.project_backend.repository.board.BoardRepository;
import mokindang.jubging.project_backend.service.board.request.BoardCreationRequest;
import mokindang.jubging.project_backend.service.board.request.BoardModificationRequest;
import mokindang.jubging.project_backend.service.board.response.BoardIdResponse;
import mokindang.jubging.project_backend.service.board.response.BoardSelectionResponse;
import mokindang.jubging.project_backend.service.board.response.MultiBoardSelectResponse;
import mokindang.jubging.project_backend.service.board.response.SummaryBoardResponse;
import mokindang.jubging.project_backend.service.member.MemberService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardService {

    private final MemberService memberService;
    private final BoardRepository boardRepository;

    @Transactional
    public BoardIdResponse write(final Long memberId, final BoardCreationRequest boardCreationRequest) {
        Member member = memberService.findByMemberId(memberId);
        LocalDateTime now = LocalDateTime.now();
        Board board = new Board(now, member, boardCreationRequest.getStartingDate(), boardCreationRequest.getActivityCategory(),
                boardCreationRequest.getTitle(), boardCreationRequest.getContent());
        Board savedBoard = boardRepository.save(board);
        return new BoardIdResponse(savedBoard.getId());
    }

    public BoardSelectionResponse select(final Long memberId, final Long boardId) {
        Member loggedInMember = memberService.findByMemberId(memberId);
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다."));
        board.checkRegion(loggedInMember.getRegion());
        return convertToBoardSelectResponse(loggedInMember, board);
    }

    private BoardSelectionResponse convertToBoardSelectResponse(final Member logindMember, final Board board) {
        return new BoardSelectionResponse(board.getId(), board.getTitle().getValue(), board.getContent().getValue(),
                board.getCreatingDateTime(), board.getWriter().getAlias(), board.getStartingDate().getValue(),
                board.getWritingRegion().getValue(), board.getActivityCategory().getValue(), board.isOnRecruitment(),
                board.getWriter().getFourLengthEmail(), board.isWriter(logindMember));
    }

    public MultiBoardSelectResponse selectAllBoards(final Pageable pageable) {
        Slice<Board> boards = boardRepository.selectBoards(pageable);
        List<SummaryBoardResponse> summaryBoards = boards.stream()
                .map(this::convertToSummaryBoard)
                .collect(Collectors.toUnmodifiableList());
        return new MultiBoardSelectResponse(summaryBoards, boards.hasNext());
    }

    private SummaryBoardResponse convertToSummaryBoard(final Board board) {
        return new SummaryBoardResponse(board.getId(), board.getTitle().getValue(), board.getContent().getValue(),
                board.getWriter().getAlias(), board.getStartingDate().getValue(), board.getWritingRegion().getValue(),
                board.getActivityCategory().getValue(), board.isOnRecruitment(), board.getWriter().getFourLengthEmail());
    }

    @Transactional
    public BoardIdResponse delete(final Long memberId, final Long boardId) {
        Member loggedInMember = memberService.findByMemberId(memberId);
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다."));
        validatePermission(board, loggedInMember, "글 작성자만 게시글을 삭제할 수 있습니다.");
        boardRepository.delete(board);
        return new BoardIdResponse(board.getId());
    }

    private void validatePermission(final Board board, final Member loggedInMember, final String message) {
        if (!board.isWriter(loggedInMember)) {
            throw new ForbiddenException(message);
        }
    }

    @Transactional
    public BoardIdResponse modify(final Long memberId, final Long boardId, final BoardModificationRequest boardModificationRequest) {
        Member loggedInMember = memberService.findByMemberId(memberId);
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다."));
        validatePermission(board, loggedInMember, "글 작성자만 게시글을 수정할 수 있습니다.");
        board.modify(boardModificationRequest.getStartingDate(), boardModificationRequest.getActivityCategory(),
                boardModificationRequest.getTitle(), boardModificationRequest.getContent());
        return new BoardIdResponse(boardId);
    }
}
