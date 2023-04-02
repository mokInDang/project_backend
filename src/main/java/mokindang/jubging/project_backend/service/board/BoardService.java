package mokindang.jubging.project_backend.service.board;

import lombok.RequiredArgsConstructor;
import mokindang.jubging.project_backend.domain.board.Board;
import mokindang.jubging.project_backend.domain.member.Member;
import mokindang.jubging.project_backend.repository.board.BoardRepository;
import mokindang.jubging.project_backend.service.board.request.BoardCreateRequest;
import mokindang.jubging.project_backend.service.board.response.BoardIdResponse;
import mokindang.jubging.project_backend.service.board.response.BoardSelectResponse;
import mokindang.jubging.project_backend.service.board.response.MultiBoardSelectResponse;
import mokindang.jubging.project_backend.service.board.response.SummaryBoardResponse;
import mokindang.jubging.project_backend.service.member.MemberService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardService {

    private final MemberService memberService;
    private final BoardRepository boardRepository;

    @Transactional
    public BoardIdResponse write(final Long memberId, final BoardCreateRequest boardCreateRequest) {
        Member member = memberService.findByMemberId(memberId);
        Board board = new Board(member, boardCreateRequest.getStartingDate(), boardCreateRequest.getActivityCategory(),
                boardCreateRequest.getTitle(), boardCreateRequest.getContent(), boardCreateRequest.getRequestDate());
        Board savedBoard = boardRepository.save(board);
        return new BoardIdResponse(savedBoard.getId());
    }

    public BoardSelectResponse select(final Long memberId, final Long boardId) {
        Member logindMember = memberService.findByMemberId(memberId);
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다."));
        board.checkRegion(logindMember.getRegion());
        return convertToBoardSelectResponse(logindMember, board);
    }

    private BoardSelectResponse convertToBoardSelectResponse(final Member logindMember, final Board board) {
        return new BoardSelectResponse(board.getId(), board.getTitle().getValue(), board.getContent().getValue(), board.getWriter().getAlias(),
                board.getStartingDate().getValue(), board.getWritingRegion().getValue(), board.getActivityCategory().getValue(),
                board.isOnRecruitment(), board.getWriter().getFourLengthEmail(), board.isWriter(logindMember));
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
}
