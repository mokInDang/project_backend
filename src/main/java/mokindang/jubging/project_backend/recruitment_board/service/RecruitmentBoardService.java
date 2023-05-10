package mokindang.jubging.project_backend.recruitment_board.service;

import lombok.RequiredArgsConstructor;
import mokindang.jubging.project_backend.member.domain.Member;
import mokindang.jubging.project_backend.member.domain.vo.Region;
import mokindang.jubging.project_backend.member.service.MemberService;
import mokindang.jubging.project_backend.recruitment_board.domain.RecruitmentBoard;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.Coordinate;
import mokindang.jubging.project_backend.recruitment_board.repository.RecruitmentBoardRepository;
import mokindang.jubging.project_backend.recruitment_board.service.request.BoardModificationRequest;
import mokindang.jubging.project_backend.recruitment_board.service.request.RecruitmentBoardCreationRequest;
import mokindang.jubging.project_backend.recruitment_board.service.response.MultiBoardSelectResponse;
import mokindang.jubging.project_backend.recruitment_board.service.response.RecruitmentBoardIdResponse;
import mokindang.jubging.project_backend.recruitment_board.service.response.RecruitmentBoardSelectionResponse;
import mokindang.jubging.project_backend.recruitment_board.service.response.SummaryBoardResponse;
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
        Coordinate meetingSpot = new Coordinate(recruitmentBoardCreationRequest.getLongitude(), recruitmentBoardCreationRequest.getLatitude());
        RecruitmentBoard recruitmentBoard = new RecruitmentBoard(now, member, recruitmentBoardCreationRequest.getStartingDate(), recruitmentBoardCreationRequest.getActivityCategory(),
                meetingSpot, recruitmentBoardCreationRequest.getTitle(), recruitmentBoardCreationRequest.getContentBody());
        RecruitmentBoard savedRecruitmentBoard = recruitmentBoardRepository.save(recruitmentBoard);
        return new RecruitmentBoardIdResponse(savedRecruitmentBoard.getId());
    }

    public RecruitmentBoardSelectionResponse select(final Long memberId, final Long boardId) {
        RecruitmentBoard recruitmentBoard = findById(boardId);
        return new RecruitmentBoardSelectionResponse(recruitmentBoard, recruitmentBoard.isSameWriterId(memberId));
    }

    public RecruitmentBoard findById(final Long boardId) {
        return recruitmentBoardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다."));
    }

    public MultiBoardSelectResponse selectAllBoards(final Pageable pageable) {
        Slice<RecruitmentBoard> recruitmentBoards = recruitmentBoardRepository.selectBoards(pageable);
        List<SummaryBoardResponse> summaryRecruitmentBoards = recruitmentBoards.stream()
                .map(SummaryBoardResponse::new)
                .collect(Collectors.toUnmodifiableList());
        return new MultiBoardSelectResponse(summaryRecruitmentBoards, recruitmentBoards.hasNext());
    }

    @Transactional
    public RecruitmentBoardIdResponse delete(final Long memberId, final Long boardId) {
        RecruitmentBoard recruitmentBoard = findById(boardId);
        recruitmentBoard.validatePermission(memberId);
        recruitmentBoardRepository.delete(recruitmentBoard);
        return new RecruitmentBoardIdResponse(recruitmentBoard.getId());
    }

    @Transactional
    public RecruitmentBoardIdResponse modify(final Long memberId, final Long boardId, final BoardModificationRequest boardModificationRequest) {
        RecruitmentBoard recruitmentBoard = findById(boardId);
        recruitmentBoard.modify(memberId, boardModificationRequest.getStartingDate(), boardModificationRequest.getActivityCategory(),
                boardModificationRequest.getTitle(), boardModificationRequest.getContentBody());
        return new RecruitmentBoardIdResponse(recruitmentBoard.getId());
    }

    @Transactional
    public RecruitmentBoardIdResponse closeRecruitment(final Long memberId, final Long boardId) {
        RecruitmentBoard recruitmentBoard = findById(boardId);
        recruitmentBoard.closeRecruitment(memberId);
        return new RecruitmentBoardIdResponse(recruitmentBoard.getId());
    }

    @Transactional
    public MultiBoardSelectResponse selectRegionBoards(final Long memberId, final Pageable pageable) {
        Member loggedInMember = memberService.findByMemberId(memberId);
        Region targetRegion = loggedInMember.getRegion();
        Slice<RecruitmentBoard> boards = recruitmentBoardRepository.selectRegionBoards(targetRegion, pageable);
        List<SummaryBoardResponse> summaryBoards = boards.stream()
                .map(SummaryBoardResponse::new)
                .collect(Collectors.toUnmodifiableList());
        return new MultiBoardSelectResponse(summaryBoards, boards.hasNext());
    }
}
