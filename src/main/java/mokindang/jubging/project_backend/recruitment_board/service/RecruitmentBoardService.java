package mokindang.jubging.project_backend.recruitment_board.service;

import lombok.RequiredArgsConstructor;
import mokindang.jubging.project_backend.comment.repository.CommentRepository;
import mokindang.jubging.project_backend.comment.repository.projectionDto.CommentCountResponse;
import mokindang.jubging.project_backend.comment.service.BoardType;
import mokindang.jubging.project_backend.member.domain.Member;
import mokindang.jubging.project_backend.member.domain.vo.Region;
import mokindang.jubging.project_backend.member.service.MemberService;
import mokindang.jubging.project_backend.participation.repository.ParticipationRepository;
import mokindang.jubging.project_backend.recruitment_board.domain.RecruitmentBoard;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.place.Coordinate;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.place.Place;
import mokindang.jubging.project_backend.recruitment_board.repository.RecruitmentBoardRepository;
import mokindang.jubging.project_backend.recruitment_board.service.request.BoardModificationRequest;
import mokindang.jubging.project_backend.recruitment_board.service.request.MeetingPlaceCreationRequest;
import mokindang.jubging.project_backend.recruitment_board.service.request.MeetingPlaceModificationRequest;
import mokindang.jubging.project_backend.recruitment_board.service.request.RecruitmentBoardCreationRequest;
import mokindang.jubging.project_backend.recruitment_board.service.response.RecruitmentBoardIdResponse;
import mokindang.jubging.project_backend.recruitment_board.service.response.board.MultiBoardSelectionResponse;
import mokindang.jubging.project_backend.recruitment_board.service.response.board.RecruitmentBoardSelectionResponse;
import mokindang.jubging.project_backend.recruitment_board.service.response.board.SummaryBoardResponse;
import mokindang.jubging.project_backend.recruitment_board.service.response.marker.BoardPlaceMarkerResponse;
import mokindang.jubging.project_backend.recruitment_board.service.response.marker.MultiBoardPlaceSelectionResponse;
import mokindang.jubging.project_backend.recruitment_board.service.response.ranking.MultiRegionCountingChartResponse;
import mokindang.jubging.project_backend.recruitment_board.service.response.ranking.RegionCountingChartResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecruitmentBoardService {

    private static final int FIRST_INDEX = 0;

    private final MemberService memberService;
    private final RecruitmentBoardRepository recruitmentBoardRepository;
    private final ParticipationRepository participationRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public RecruitmentBoardIdResponse write(final Long memberId, final RecruitmentBoardCreationRequest recruitmentBoardCreationRequest) {
        Member member = memberService.findByMemberId(memberId);
        LocalDateTime now = LocalDateTime.now();
        Place meetingPlace = generateCreationPlace(recruitmentBoardCreationRequest.getMeetingPlaceCreationRequest());
        RecruitmentBoard recruitmentBoard = new RecruitmentBoard(now, member, recruitmentBoardCreationRequest.getStartingDate(),
                recruitmentBoardCreationRequest.getActivityCategory(), meetingPlace, recruitmentBoardCreationRequest.getTitle(),
                recruitmentBoardCreationRequest.getContentBody(), recruitmentBoardCreationRequest.getMaxOfParticipationCount());
        RecruitmentBoard savedRecruitmentBoard = recruitmentBoardRepository.save(recruitmentBoard);
        return new RecruitmentBoardIdResponse(savedRecruitmentBoard.getId());
    }

    private Place generateCreationPlace(final MeetingPlaceCreationRequest meetingPlaceCreationRequest) {
        Coordinate meetingSpot = new Coordinate(meetingPlaceCreationRequest.getLongitude(),
                meetingPlaceCreationRequest.getLatitude());
        String meetingAddress = meetingPlaceCreationRequest.getMeetingAddress();
        return new Place(meetingSpot, meetingAddress);
    }

    public RecruitmentBoardSelectionResponse select(final Long memberId, final Long boardId) {
        RecruitmentBoard recruitmentBoard = findByIdWithOptimisticLock(boardId);
        Member member = memberService.findByMemberId(memberId);
        return new RecruitmentBoardSelectionResponse(member, recruitmentBoard);
    }

    public RecruitmentBoard findByIdWithOptimisticLock(final Long boardId) {
        return recruitmentBoardRepository.findByIdWithOptimisticLock(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다."));
    }

    public MultiBoardSelectionResponse selectAllBoards(final Pageable pageable) {
        Slice<RecruitmentBoard> boards = recruitmentBoardRepository.selectBoards(pageable);
        List<SummaryBoardResponse> summaryRecruitmentBoards = convertToSummaryBoardResponses(boards.getContent());
        return new MultiBoardSelectionResponse(summaryRecruitmentBoards, boards.hasNext());
    }

    private List<SummaryBoardResponse> convertToSummaryBoardResponses(final List<RecruitmentBoard> boards) {
        List<CommentCountResponse> commentCountResponses = getCommentCountResponse(boards);
        return IntStream.range(FIRST_INDEX, boards.size())
                .mapToObj(index -> new SummaryBoardResponse(boards.get(index), commentCountResponses.get(index).getCommentCount())
                ).collect(Collectors.toUnmodifiableList());
    }

    private List<CommentCountResponse> getCommentCountResponse(final List<RecruitmentBoard> boards) {
        List<Long> boardIds = boards.stream()
                .map(board -> board.getId())
                .collect(Collectors.toUnmodifiableList());
        return commentRepository.countALLCommentByBoardIds(boardIds, BoardType.RECRUITMENT_BOARD);
    }

    @Transactional
    public RecruitmentBoardIdResponse delete(final Long memberId, final Long boardId) {
        RecruitmentBoard recruitmentBoard = findByIdWithOptimisticLock(boardId);
        recruitmentBoard.validatePermission(memberId);
        recruitmentBoardRepository.delete(recruitmentBoard);
        participationRepository.deleteAllByRecruitmentBoardId(boardId);
        return new RecruitmentBoardIdResponse(recruitmentBoard.getId());
    }

    @Transactional
    public RecruitmentBoardIdResponse modify(final Long memberId, final Long boardId, final BoardModificationRequest boardModificationRequest) {
        RecruitmentBoard recruitmentBoard = findByIdWithOptimisticLock(boardId);
        Place place = generateModificationPlace(boardModificationRequest.getMeetingPlaceModificationRequest());
        recruitmentBoard.modify(memberId, boardModificationRequest.getStartingDate(), boardModificationRequest.getActivityCategory(),
                boardModificationRequest.getTitle(), boardModificationRequest.getContentBody(), place);
        return new RecruitmentBoardIdResponse(recruitmentBoard.getId());
    }

    private Place generateModificationPlace(final MeetingPlaceModificationRequest meetingPlaceModificationRequest) {
        Coordinate meetingSpot = new Coordinate(meetingPlaceModificationRequest.getLongitude(),
                meetingPlaceModificationRequest.getLatitude());
        String meetingAddress = meetingPlaceModificationRequest.getMeetingAddress();
        return new Place(meetingSpot, meetingAddress);
    }

    @Transactional
    public RecruitmentBoardIdResponse closeRecruitment(final Long memberId, final Long boardId) {
        RecruitmentBoard recruitmentBoard = findByIdWithOptimisticLock(boardId);
        recruitmentBoard.closeRecruitment(memberId);
        return new RecruitmentBoardIdResponse(recruitmentBoard.getId());
    }

    public MultiBoardSelectionResponse selectRegionBoards(final Long memberId, final Pageable pageable) {
        Member loggedInMember = memberService.findByMemberId(memberId);
        Region targetRegion = loggedInMember.getRegion();
        Slice<RecruitmentBoard> boards = recruitmentBoardRepository.selectRegionBoards(targetRegion, pageable);
        List<SummaryBoardResponse> summaryBoardResponses = convertToSummaryBoardResponses(boards.getContent());
        return new MultiBoardSelectionResponse(summaryBoardResponses, boards.hasNext());
    }

    public MultiBoardPlaceSelectionResponse selectRegionBoardsCloseToDeadline(final Long memberId, final Pageable pageable) {
        Member member = memberService.findByMemberId(memberId);
        Slice<RecruitmentBoard> recruitmentBoards = recruitmentBoardRepository.selectRecruitmentRegionBoardsCloseToDeadline(member.getRegion(), pageable);
        List<BoardPlaceMarkerResponse> boardPlaceMarkerResponses = recruitmentBoards.stream()
                .map(BoardPlaceMarkerResponse::new)
                .collect(Collectors.toUnmodifiableList());
        return new MultiBoardPlaceSelectionResponse(boardPlaceMarkerResponses, recruitmentBoards.hasNext());
    }

    public MultiRegionCountingChartResponse getRegionCountingChart(final Pageable pageable) {
        Slice<Region> regionBoardsCountingChart = recruitmentBoardRepository.getRegionBoardsCountingChart(pageable);
        List<RegionCountingChartResponse> regionCountingChartResponses = regionBoardsCountingChart.stream()
                .map(region -> new RegionCountingChartResponse(region.getValue()))
                .collect(Collectors.toUnmodifiableList());
        return new MultiRegionCountingChartResponse(regionCountingChartResponses, regionBoardsCountingChart.hasNext());
    }

    @Transactional
    public RecruitmentBoardIdResponse participate(final Long memberId, final Long boardId) {
        RecruitmentBoard board = findByIdWithOptimisticLock(boardId);
        Member member = memberService.findByMemberId(memberId);
        board.addParticipationMember(member);
        return new RecruitmentBoardIdResponse(board.getId());
    }
}
