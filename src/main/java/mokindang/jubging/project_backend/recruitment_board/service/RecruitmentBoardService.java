package mokindang.jubging.project_backend.recruitment_board.service;

import lombok.RequiredArgsConstructor;
import mokindang.jubging.project_backend.member.domain.Member;
import mokindang.jubging.project_backend.member.domain.vo.Region;
import mokindang.jubging.project_backend.member.service.MemberService;
import mokindang.jubging.project_backend.recruitment_board.domain.RecruitmentBoard;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.Coordinate;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.Place;
import mokindang.jubging.project_backend.recruitment_board.repository.RecruitmentBoardRepository;
import mokindang.jubging.project_backend.recruitment_board.service.request.BoardModificationRequest;
import mokindang.jubging.project_backend.recruitment_board.service.request.MeetingPlaceCreationRequest;
import mokindang.jubging.project_backend.recruitment_board.service.request.MeetingPlaceModificationRequest;
import mokindang.jubging.project_backend.recruitment_board.service.request.RecruitmentBoardCreationRequest;
import mokindang.jubging.project_backend.recruitment_board.service.response.*;
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
        Place meetingPlace = generateCreationPlace(recruitmentBoardCreationRequest.getMeetingPlaceCreationRequest());
        RecruitmentBoard recruitmentBoard = new RecruitmentBoard(now, member, recruitmentBoardCreationRequest.getStartingDate(),
                recruitmentBoardCreationRequest.getActivityCategory(), meetingPlace, recruitmentBoardCreationRequest.getTitle(), recruitmentBoardCreationRequest.getContentBody());
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
        RecruitmentBoard recruitmentBoard = findById(boardId);
        return new RecruitmentBoardSelectionResponse(recruitmentBoard, recruitmentBoard.isSameWriterId(memberId));
    }

    public RecruitmentBoard findById(final Long boardId) {
        return recruitmentBoardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다."));
    }

    public MultiBoardSelectionResponse selectAllBoards(final Pageable pageable) {
        Slice<RecruitmentBoard> recruitmentBoards = recruitmentBoardRepository.selectBoards(pageable);
        List<SummaryBoardResponse> summaryRecruitmentBoards = recruitmentBoards.stream()
                .map(SummaryBoardResponse::new)
                .collect(Collectors.toUnmodifiableList());
        return new MultiBoardSelectionResponse(summaryRecruitmentBoards, recruitmentBoards.hasNext());
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
        RecruitmentBoard recruitmentBoard = findById(boardId);
        recruitmentBoard.closeRecruitment(memberId);
        return new RecruitmentBoardIdResponse(recruitmentBoard.getId());
    }

    public MultiBoardSelectionResponse selectRegionBoards(final Long memberId, final Pageable pageable) {
        Member loggedInMember = memberService.findByMemberId(memberId);
        Region targetRegion = loggedInMember.getRegion();
        Slice<RecruitmentBoard> boards = recruitmentBoardRepository.selectRegionBoards(targetRegion, pageable);
        List<SummaryBoardResponse> summaryBoards = boards.stream()
                .map(SummaryBoardResponse::new)
                .collect(Collectors.toUnmodifiableList());
        return new MultiBoardSelectionResponse(summaryBoards, boards.hasNext());
    }

    public MultiBoardPlaceSelectionResponse selectRegionBoardsCloseToDeadline(final Long memberId, final Pageable pageable) {
        Member member = memberService.findByMemberId(memberId);
        Slice<RecruitmentBoard> recruitmentBoards = recruitmentBoardRepository.selectRecruitmentRegionBoardsCloseToDeadline(member.getRegion(), pageable);
        List<BoardPlaceMarkerResponse> boardPlaceMarkerResponses = recruitmentBoards.stream()
                .map(BoardPlaceMarkerResponse::new)
                .collect(Collectors.toUnmodifiableList());
        return new MultiBoardPlaceSelectionResponse(boardPlaceMarkerResponses, recruitmentBoards.hasNext());
    }

    public boolean hasWritingCommentPermission(final Long memberId, final Long boardId) {
        Member member = memberService.findByMemberId(memberId);
        RecruitmentBoard board = findById(boardId);
        return board.isSameRegion(member.getRegion());
    }

    public List<RegionCountChartResponse> getRegionCountChart(final Pageable pageable) {
        List<Region> regionBoardsCountChart = recruitmentBoardRepository.getRegionBoardsCountChart(pageable);
        List<RegionCountChartResponse> regionCountChartResponses = regionBoardsCountChart.stream()
                .map(RegionCountChartResponse::new)
                .collect(Collectors.toUnmodifiableList());
        return regionCountChartResponses;
    }
}
