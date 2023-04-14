package mokindang.jubging.project_backend.service.board;

import lombok.RequiredArgsConstructor;
import mokindang.jubging.project_backend.domain.board.RecruitmentBoard;
import mokindang.jubging.project_backend.domain.member.Member;
import mokindang.jubging.project_backend.domain.member.vo.Region;
import mokindang.jubging.project_backend.exception.custom.ForbiddenException;
import mokindang.jubging.project_backend.repository.board.RecruitmentBoardRepository;
import mokindang.jubging.project_backend.service.board.request.RecruitmentBoardCreationRequest;
import mokindang.jubging.project_backend.service.board.request.BoardModificationRequest;
import mokindang.jubging.project_backend.service.board.response.RecruitmentBoardIdResponse;
import mokindang.jubging.project_backend.service.board.response.RecruitmentBoardSelectionResponse;
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
public class RecruitmentBoardService {

    private final MemberService memberService;
    private final RecruitmentBoardRepository recruitmentBoardRepository;

    @Transactional
    public RecruitmentBoardIdResponse write(final Long memberId, final RecruitmentBoardCreationRequest recruitmentBoardCreationRequest) {
        Member member = memberService.findByMemberId(memberId);
        LocalDateTime now = LocalDateTime.now();
        RecruitmentBoard recruitmentBoard = new RecruitmentBoard(now, member, recruitmentBoardCreationRequest.getStartingDate(), recruitmentBoardCreationRequest.getActivityCategory(),
                recruitmentBoardCreationRequest.getTitle(), recruitmentBoardCreationRequest.getContent());
        RecruitmentBoard savedRecruitmentBoard = recruitmentBoardRepository.save(recruitmentBoard);
        return new RecruitmentBoardIdResponse(savedRecruitmentBoard.getId());
    }

    public RecruitmentBoardSelectionResponse select(final Long memberId, final Long boardId) {
        Member loggedInMember = memberService.findByMemberId(memberId);
        RecruitmentBoard recruitmentBoard = findById(boardId);
        recruitmentBoard.checkRegion(loggedInMember.getRegion());
        return convertToBoardSelectResponse(loggedInMember, recruitmentBoard);
    }

    private RecruitmentBoard findById(final Long boardId) {
        return recruitmentBoardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다."));
    }

    private RecruitmentBoardSelectionResponse convertToBoardSelectResponse(final Member logindMember, final RecruitmentBoard recruitmentBoard) {
        return new RecruitmentBoardSelectionResponse(recruitmentBoard.getId(), recruitmentBoard.getTitle().getValue(), recruitmentBoard.getContent().getValue(),
                recruitmentBoard.getCreatingDateTime(), recruitmentBoard.getWriter().getAlias(), recruitmentBoard.getStartingDate().getValue(),
                recruitmentBoard.getWritingRegion().getValue(), recruitmentBoard.getActivityCategory().getValue(), recruitmentBoard.isOnRecruitment(),
                recruitmentBoard.getWriter().getFourLengthEmail(), recruitmentBoard.getWriterProfileImageUrl(), recruitmentBoard.isWriter(logindMember));
    }

    public MultiBoardSelectResponse selectAllBoards(final Pageable pageable) {
        Slice<RecruitmentBoard> recruitmentBoards = recruitmentBoardRepository.selectBoards(pageable);
        List<SummaryBoardResponse> summaryRecruitmentBoards = recruitmentBoards.stream()
                .map(this::convertToSummaryRecruitmentBoard)
                .collect(Collectors.toUnmodifiableList());
        return new MultiBoardSelectResponse(summaryRecruitmentBoards, recruitmentBoards.hasNext());
    }

    private SummaryBoardResponse convertToSummaryRecruitmentBoard(final RecruitmentBoard recruitmentBoard) {
        return new SummaryBoardResponse(recruitmentBoard.getId(), recruitmentBoard.getTitle().getValue(), recruitmentBoard.getContent().getValue(),
                recruitmentBoard.getWriter().getAlias(), recruitmentBoard.getWriterProfileImageUrl(), recruitmentBoard.getStartingDate().getValue(),
                recruitmentBoard.getWritingRegion().getValue(), recruitmentBoard.getActivityCategory().getValue(),
                recruitmentBoard.isOnRecruitment(), recruitmentBoard.getWriter().getFourLengthEmail());
    }

    @Transactional
    public RecruitmentBoardIdResponse delete(final Long memberId, final Long boardId) {
        Member loggedInMember = memberService.findByMemberId(memberId);
        RecruitmentBoard recruitmentBoard = findById(boardId);
        validatePermission(recruitmentBoard, loggedInMember, "글 작성자만 게시글을 삭제할 수 있습니다.");
        recruitmentBoardRepository.delete(recruitmentBoard);
        return new RecruitmentBoardIdResponse(recruitmentBoard.getId());
    }

    private void validatePermission(final RecruitmentBoard recruitmentBoard, final Member loggedInMember, final String message) {
        if (!recruitmentBoard.isWriter(loggedInMember)) {
            throw new ForbiddenException(message);
        }
    }

    @Transactional
    public RecruitmentBoardIdResponse modify(final Long memberId, final Long boardId, final BoardModificationRequest boardModificationRequest) {
        Member loggedInMember = memberService.findByMemberId(memberId);
        RecruitmentBoard recruitmentBoard = findById(boardId);
        validatePermission(recruitmentBoard, loggedInMember, "글 작성자만 게시글을 수정할 수 있습니다.");
        recruitmentBoard.modify(boardModificationRequest.getStartingDate(), boardModificationRequest.getActivityCategory(),
                boardModificationRequest.getTitle(), boardModificationRequest.getContent());
        return new RecruitmentBoardIdResponse(recruitmentBoard.getId());
    }

    @Transactional
    public RecruitmentBoardIdResponse closeRecruitment(final Long memberId, final Long boardId) {
        Member loggedInMember = memberService.findByMemberId(memberId);
        RecruitmentBoard recruitmentBoard = findById(boardId);
        validatePermission(recruitmentBoard, loggedInMember, "글 작성자만 모집 마감할 수 있습니다.");
        recruitmentBoard.closeRecruitment();
        return new RecruitmentBoardIdResponse(recruitmentBoard.getId());
    }

    @Transactional
    public MultiBoardSelectResponse selectRegionBoards(final Long memberId, final Pageable pageable) {
        Member loggedInMember = memberService.findByMemberId(memberId);
        Region targetRegion = loggedInMember.getRegion();
        Slice<RecruitmentBoard> boards = recruitmentBoardRepository.selectRegionBoards(targetRegion, pageable);
        List<SummaryBoardResponse> summaryBoards = boards.stream()
                .map(this::convertToSummaryRecruitmentBoard)
                .collect(Collectors.toUnmodifiableList());
        return new MultiBoardSelectResponse(summaryBoards, boards.hasNext());
    }
}
