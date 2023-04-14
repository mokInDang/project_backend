package mokindang.jubging.project_backend.service.board;

import lombok.RequiredArgsConstructor;
import mokindang.jubging.project_backend.domain.board.RecruitmentBoard;
import mokindang.jubging.project_backend.domain.member.Member;
import mokindang.jubging.project_backend.domain.member.vo.Region;
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
        RecruitmentBoard recruitmentBoard = new RecruitmentBoard(now, member, boardCreationRequest.getStartingDate(), boardCreationRequest.getActivityCategory(),
                boardCreationRequest.getTitle(), boardCreationRequest.getContent());
        RecruitmentBoard savedRecruitmentBoard = boardRepository.save(recruitmentBoard);
        return new BoardIdResponse(savedRecruitmentBoard.getId());
    }

    public BoardSelectionResponse select(final Long memberId, final Long boardId) {
        Member loggedInMember = memberService.findByMemberId(memberId);
        RecruitmentBoard recruitmentBoard = findById(boardId);
        recruitmentBoard.checkRegion(loggedInMember.getRegion());
        return convertToBoardSelectResponse(loggedInMember, recruitmentBoard);
    }

    private RecruitmentBoard findById(final Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다."));
    }

    private BoardSelectionResponse convertToBoardSelectResponse(final Member logindMember, final RecruitmentBoard recruitmentBoard) {
        return new BoardSelectionResponse(recruitmentBoard.getId(), recruitmentBoard.getTitle().getValue(), recruitmentBoard.getContent().getValue(),
                recruitmentBoard.getCreatingDateTime(), recruitmentBoard.getWriter().getAlias(), recruitmentBoard.getStartingDate().getValue(),
                recruitmentBoard.getWritingRegion().getValue(), recruitmentBoard.getActivityCategory().getValue(), recruitmentBoard.isOnRecruitment(),
                recruitmentBoard.getWriter().getFourLengthEmail(), recruitmentBoard.getWriterProfileImageUrl(), recruitmentBoard.isWriter(logindMember));
    }

    public MultiBoardSelectResponse selectAllBoards(final Pageable pageable) {
        Slice<RecruitmentBoard> boards = boardRepository.selectBoards(pageable);
        List<SummaryBoardResponse> summaryBoards = boards.stream()
                .map(this::convertToSummaryBoard)
                .collect(Collectors.toUnmodifiableList());
        return new MultiBoardSelectResponse(summaryBoards, boards.hasNext());
    }

    private SummaryBoardResponse convertToSummaryBoard(final RecruitmentBoard recruitmentBoard) {
        return new SummaryBoardResponse(recruitmentBoard.getId(), recruitmentBoard.getTitle().getValue(), recruitmentBoard.getContent().getValue(),
                recruitmentBoard.getWriter().getAlias(), recruitmentBoard.getWriterProfileImageUrl(), recruitmentBoard.getStartingDate().getValue(),
                recruitmentBoard.getWritingRegion().getValue(), recruitmentBoard.getActivityCategory().getValue(),
                recruitmentBoard.isOnRecruitment(), recruitmentBoard.getWriter().getFourLengthEmail());
    }

    @Transactional
    public BoardIdResponse delete(final Long memberId, final Long boardId) {
        Member loggedInMember = memberService.findByMemberId(memberId);
        RecruitmentBoard recruitmentBoard = findById(boardId);
        validatePermission(recruitmentBoard, loggedInMember, "글 작성자만 게시글을 삭제할 수 있습니다.");
        boardRepository.delete(recruitmentBoard);
        return new BoardIdResponse(recruitmentBoard.getId());
    }

    private void validatePermission(final RecruitmentBoard recruitmentBoard, final Member loggedInMember, final String message) {
        if (!recruitmentBoard.isWriter(loggedInMember)) {
            throw new ForbiddenException(message);
        }
    }

    @Transactional
    public BoardIdResponse modify(final Long memberId, final Long boardId, final BoardModificationRequest boardModificationRequest) {
        Member loggedInMember = memberService.findByMemberId(memberId);
        RecruitmentBoard recruitmentBoard = findById(boardId);
        validatePermission(recruitmentBoard, loggedInMember, "글 작성자만 게시글을 수정할 수 있습니다.");
        recruitmentBoard.modify(boardModificationRequest.getStartingDate(), boardModificationRequest.getActivityCategory(),
                boardModificationRequest.getTitle(), boardModificationRequest.getContent());
        return new BoardIdResponse(recruitmentBoard.getId());
    }

    @Transactional
    public BoardIdResponse closeRecruitment(final Long memberId, final Long boardId) {
        Member loggedInMember = memberService.findByMemberId(memberId);
        RecruitmentBoard recruitmentBoard = findById(boardId);
        validatePermission(recruitmentBoard, loggedInMember, "글 작성자만 모집 마감할 수 있습니다.");
        recruitmentBoard.closeRecruitment();
        return new BoardIdResponse(recruitmentBoard.getId());
    }

    @Transactional
    public MultiBoardSelectResponse selectRegionBoards(final Long memberId, final Pageable pageable) {
        Member loggedInMember = memberService.findByMemberId(memberId);
        Region targetRegion = loggedInMember.getRegion();
        Slice<RecruitmentBoard> boards = boardRepository.selectRegionBoards(targetRegion, pageable);
        List<SummaryBoardResponse> summaryBoards = boards.stream()
                .map(this::convertToSummaryBoard)
                .collect(Collectors.toUnmodifiableList());
        return new MultiBoardSelectResponse(summaryBoards, boards.hasNext());
    }
}
