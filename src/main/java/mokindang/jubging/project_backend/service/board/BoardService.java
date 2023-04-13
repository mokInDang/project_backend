package mokindang.jubging.project_backend.service.board;

import lombok.RequiredArgsConstructor;
import mokindang.jubging.project_backend.domain.board.Board;
import mokindang.jubging.project_backend.domain.member.Member;
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
        Board board = findById(boardId);
        board.checkRegion(loggedInMember.getRegion());
        return new BoardSelectionResponse(board, board.isSameWriterId(memberId));
    }

    private Board findById(final Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다."));
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
                board.getWriter().getAlias(), board.getWriterProfileImageUrl(), board.getStartingDate().getValue(),
                board.getWritingRegion().getValue(), board.getActivityCategory().getValue(),
                board.isOnRecruitment(), board.getWriter().getFirstFourDigitsOfWriterEmail());
    }

    @Transactional
    public BoardIdResponse delete(final Long memberId, final Long boardId) {
        Board board = findById(boardId);
        board.validatePermission(memberId);
        boardRepository.delete(board);
        return new BoardIdResponse(board.getId());
    }

    @Transactional
    public BoardIdResponse modify(final Long memberId, final Long boardId, final BoardModificationRequest boardModificationRequest) {
        Board board = findById(boardId);
        board.modify(memberId, boardModificationRequest.getStartingDate(), boardModificationRequest.getActivityCategory(),
                boardModificationRequest.getTitle(), boardModificationRequest.getContent());
        return new BoardIdResponse(board.getId());
    }

    @Transactional
    public BoardIdResponse closeRecruitment(final Long memberId, final Long boardId) {
        Board board = findById(boardId);
        board.closeRecruitment(memberId);
        return new BoardIdResponse(board.getId());
    }
}
