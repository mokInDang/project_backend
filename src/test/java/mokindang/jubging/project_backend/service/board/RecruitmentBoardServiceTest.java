package mokindang.jubging.project_backend.service.board;

import mokindang.jubging.project_backend.member.domain.Member;
import mokindang.jubging.project_backend.member.domain.vo.Region;
import mokindang.jubging.project_backend.member.service.MemberService;
import mokindang.jubging.project_backend.recruitment_board.domain.ActivityCategory;
import mokindang.jubging.project_backend.recruitment_board.domain.RecruitmentBoard;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.ContentBody;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.ParticipationCount;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.StartingDate;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.Title;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.place.Coordinate;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.place.Place;
import mokindang.jubging.project_backend.recruitment_board.repository.RecruitmentBoardRepository;
import mokindang.jubging.project_backend.recruitment_board.service.RecruitmentBoardService;
import mokindang.jubging.project_backend.recruitment_board.service.request.BoardModificationRequest;
import mokindang.jubging.project_backend.recruitment_board.service.request.MeetingPlaceCreationRequest;
import mokindang.jubging.project_backend.recruitment_board.service.request.MeetingPlaceModificationRequest;
import mokindang.jubging.project_backend.recruitment_board.service.request.RecruitmentBoardCreationRequest;
import mokindang.jubging.project_backend.recruitment_board.service.response.MultiBoardPlaceSelectionResponse;
import mokindang.jubging.project_backend.recruitment_board.service.response.MultiBoardSelectionResponse;
import mokindang.jubging.project_backend.recruitment_board.service.response.RecruitmentBoardIdResponse;
import mokindang.jubging.project_backend.recruitment_board.service.response.RecruitmentBoardSelectionResponse;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecruitmentBoardServiceTest {

    @Mock
    private MemberService memberService;

    @Mock
    private RecruitmentBoardRepository boardRepository;

    @InjectMocks
    private RecruitmentBoardService boardService;

    @Test
    @Transactional
    @DisplayName("해당하는 유저가 작성한 게시글을 저장한다. 저장 후 작성된 게시글 번호를 반환한다.")
    void write() {
        //given
        Member member = mock(Member.class);
        Region region = mock(Region.class);
        when(member.getRegion()).thenReturn(region);
        when(region.isDefault()).thenReturn(false);
        when(memberService.findByMemberId(anyLong())).thenReturn(member);
        RecruitmentBoard savedBoard = mock(RecruitmentBoard.class);
        when(savedBoard.getId()).thenReturn(1L);
        when(boardRepository.save(any(RecruitmentBoard.class))).thenReturn(savedBoard);

        RecruitmentBoardCreationRequest recruitmentBoardCreationRequest = createTestRecruitmentBoardCreationRequest();

        //when
        RecruitmentBoardIdResponse savedBoardId = boardService.write(1L, recruitmentBoardCreationRequest);

        //then
        assertThat(savedBoardId.getBoardId()).isEqualTo(1L);
        verify(boardRepository, times(1)).save(any());
    }

    private RecruitmentBoardCreationRequest createTestRecruitmentBoardCreationRequest() {
        MeetingPlaceCreationRequest meetingPlaceCreationRequest = createTestMeetingPlaceCreationRequest();
        return new RecruitmentBoardCreationRequest("제목", "본문", "달리기",
                LocalDate.of(2023, 11, 11), meetingPlaceCreationRequest, 8);
    }

    private MeetingPlaceCreationRequest createTestMeetingPlaceCreationRequest() {
        return new MeetingPlaceCreationRequest(1.1, 1.2, "서울시 동작구 상도동 1-1");
    }

    @Test
    @DisplayName("존재하지 않는 유저가 게시글 저장 시, 예외를 발생한다.")
    void writeFailedByNonexistentMember() {
        //given
        when(memberService.findByMemberId(anyLong())).thenThrow(new IllegalArgumentException("해당하는 유저가 존재하지 않습니다."));

        RecruitmentBoardCreationRequest boardCreationRequest = createTestRecruitmentBoardCreationRequest();

        //when, then
        assertThatThrownBy(() -> boardService.write(1L, boardCreationRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당하는 유저가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("게시글을 조회한다.")
    void selectBoardId() {
        //given
        SoftAssertions softly = new SoftAssertions();
        RecruitmentBoard recruitmentBoard = createMockedBoard();
        when(boardRepository.findByIdWithOptimisticLock(1L)).thenReturn(Optional.of(recruitmentBoard));
        Member member = createMockedMember();
        when(memberService.findByMemberId(1L)).thenReturn(member);

        //when
        RecruitmentBoardSelectionResponse actual = boardService.select(1L, 1L);

        //then
        softly.assertThat(actual.getBoardId()).isEqualTo(1L);
        softly.assertThat(actual.getTitle()).isEqualTo("제목");
        softly.assertThat(actual.getContentBody()).isEqualTo("본문내용");
        softly.assertThat(actual.getWriterAlias()).isEqualTo("test");
        softly.assertThat(actual.getStartingDate()).isEqualTo("2025-02-11");
        softly.assertThat(actual.getActivityCategory()).isEqualTo("달리기");
        softly.assertThat(actual.isOnRecruitment()).isEqualTo(true);
        softly.assertThat(actual.getWriterProfileImageUrl()).isEqualTo("test_url");
        softly.assertThat(actual.getFirstFourLettersOfEmail()).isEqualTo("test");
        softly.assertThat(actual.getMeetingPlaceResponse().getLongitude()).isEqualTo(1.1);
        softly.assertThat(actual.getMeetingPlaceResponse().getLatitude()).isEqualTo(1.2);
        softly.assertThat(actual.getMeetingPlaceResponse().getMeetingAddress()).isEqualTo("서울시 동작구 상도동 1-1");
        softly.assertThat(actual.isMine()).isEqualTo(true);
        softly.assertThat(actual.getParticipationCount()).isEqualTo(1);
        softly.assertThat(actual.getMaxOfParticipationCount()).isEqualTo(8);
        softly.assertAll();
    }

    private Member createMockedMember() {
        Member member = mock(Member.class);
        when(member.getId()).thenReturn(1L);
        when(member.getRegion()).thenReturn(Region.from("동작구"));
        return member;
    }

    private RecruitmentBoard createMockedBoard() {
        LocalDate today = LocalDate.of(2023, 3, 14);
        RecruitmentBoard recruitmentBoard = mock(RecruitmentBoard.class);
        when(recruitmentBoard.getId()).thenReturn(1L);
        when(recruitmentBoard.getTitle()).thenReturn(new Title("제목"));
        when(recruitmentBoard.getContentBody()).thenReturn(new ContentBody("본문내용"));
        when(recruitmentBoard.getWritingRegion()).thenReturn(Region.from("동작구"));
        when(recruitmentBoard.getActivityCategory()).thenReturn(ActivityCategory.RUNNING);
        when(recruitmentBoard.isOnRecruitment()).thenReturn(true);
        when(recruitmentBoard.getCreatingDateTime()).thenReturn(LocalDateTime.of(2023, 11, 12, 0, 0, 0));
        when(recruitmentBoard.getStartingDate()).thenReturn(new StartingDate(today, LocalDate.of(2025, 2, 11)));
        when(recruitmentBoard.getWriterAlias()).thenReturn("test");
        when(recruitmentBoard.getFirstFourDigitsOfWriterEmail()).thenReturn("test");
        when(recruitmentBoard.getWriterProfileImageUrl()).thenReturn("test_url");
        when(recruitmentBoard.getMeetingPlace()).thenReturn(createTestPlace());
        when(recruitmentBoard.getParticipationCount()).thenReturn(ParticipationCount.createDefaultParticipationCount(8));
        when(recruitmentBoard.isSameWriterId(1L)).thenReturn(true);
        when(recruitmentBoard.isParticipatedIn(1L)).thenReturn(true);
        return recruitmentBoard;
    }

    private Place createTestPlace() {
        Coordinate coordinate = new Coordinate(1.1, 1.2);
        return new Place(coordinate, "서울시 동작구 상도동 1-1");
    }

    @Test
    @DisplayName("게시글 작성자가 게시글 삭제를 요청한 경우 게시글을 삭제한다." +
            " 삭제 후 삭제된 게시글 Id를 가진 BoardIdResponse 를 반환한다.")
    void delete() {
        //given
        RecruitmentBoard recruitmentBoard = mock(RecruitmentBoard.class);
        when(boardRepository.findByIdWithOptimisticLock(anyLong())).thenReturn(Optional.of(recruitmentBoard));
        Long boardId = 1L;
        when(recruitmentBoard.getId()).thenReturn(boardId);
        Long writerId = 1L;

        //when
        RecruitmentBoardIdResponse deleteBoardIdResponse = boardService.delete(writerId, boardId);

        //then
        assertThat(deleteBoardIdResponse.getBoardId()).isEqualTo(boardId);
        verify(boardRepository, times(1)).delete(any());
    }

    @Test
    @DisplayName("게시글 삭제 요청 시, 존재하지 않는 게시물에 대한 접근이면 예외를 발생한다.")
    void deleteFailedByNonexistentBoard() {
        //given
        when(boardRepository.findByIdWithOptimisticLock(anyLong())).thenThrow(new IllegalArgumentException("존재하지 않는 게시물 입니다."));

        //when, then
        assertThatThrownBy(() -> boardService.delete(1L, 1L)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 게시물 입니다.");
    }

    @Test
    @DisplayName("게시글을 수정한다.")
    void modify() {
        //given
        RecruitmentBoard recruitmentBoard = mock(RecruitmentBoard.class);
        when(boardRepository.findByIdWithOptimisticLock(anyLong())).thenReturn(Optional.of(recruitmentBoard));
        when(recruitmentBoard.getId()).thenReturn(1L);

        Long memberId = 1L;
        Long boardId = 1L;
        BoardModificationRequest boardModificationRequest = createTestModificationRequest();

        //when
        RecruitmentBoardIdResponse actual = boardService.modify(memberId, boardId, boardModificationRequest);

        //then
        assertThat(actual.getBoardId()).isEqualTo(1L);
        verify(recruitmentBoard, times(1)).modify(anyLong(), any(), any(), any(), any(), any());
    }

    private BoardModificationRequest createTestModificationRequest() {
        MeetingPlaceModificationRequest meetingPlaceModificationRequest = new MeetingPlaceModificationRequest(1.1, 1.2, "서울시 동작구 상도동 1-1");

        return new BoardModificationRequest("새로운 제목", "새로운 본문",
                "산책", LocalDate.of(2023, 1, 1), meetingPlaceModificationRequest);
    }


    @Test
    @DisplayName("게시글의 모집을 마감한다.")
    void closeRecruitment() {
        //given
        RecruitmentBoard recruitmentBoard = mock(RecruitmentBoard.class);
        when(recruitmentBoard.getId()).thenReturn(1L);
        when(boardRepository.findByIdWithOptimisticLock(anyLong())).thenReturn(Optional.of(recruitmentBoard));

        Long memberId = 1L;
        Long boardId = 1L;

        //when
        RecruitmentBoardIdResponse boardIdResponse = boardService.closeRecruitment(memberId, boardId);

        //then
        assertThat(boardIdResponse.getBoardId()).isEqualTo(1L);
        verify(recruitmentBoard, times(1)).closeRecruitment(memberId);
    }

    @Test
    @DisplayName("게시글 모집 마감 요청 시, 입력 받은 boardId 를 가진 게시글이 존재하지 않는다면 예외를 반환한다.")
    void closeRecruitmentFailedByNoneExistBoard() {
        //given
        RecruitmentBoard recruitmentBoard = mock(RecruitmentBoard.class);
        when(boardRepository.findByIdWithOptimisticLock(anyLong())).thenReturn(Optional.empty());

        Long memberId = 1L;
        Long boardId = 1L;

        assertThatThrownBy(() -> boardService.closeRecruitment(memberId, boardId)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 게시물입니다.");
    }

    @Test
    @DisplayName("요청 회원의 지역에 해당하는 게시글 리스트를 반환한다.")
    void selectRegionBoards() {
        //given
        LocalDateTime now = LocalDateTime.of(2023, 3, 25, 1, 1);
        Member dongJackMember = new Member("test@mail.com", "동작이");
        dongJackMember.updateRegion("동작구");
        RecruitmentBoard dongJackBoard1 = new RecruitmentBoard(now.plusDays(1), dongJackMember,
                LocalDate.of(2023, 3, 27), "달리기", createTestPlace(), "제목1", "본문1", 8);
        RecruitmentBoard dongJackBoard2 = new RecruitmentBoard(now, dongJackMember,
                LocalDate.of(2023, 3, 27), "산책", createTestPlace(), "제목2", "본문2", 8);
        Slice<RecruitmentBoard> slice = new SliceImpl<>(List.of(dongJackBoard1, dongJackBoard2));

        Member member = mock(Member.class);
        when(member.getRegion()).thenReturn(Region.from("동작구"));
        when(memberService.findByMemberId(1L)).thenReturn(member);
        when(boardRepository.selectRegionBoards(any(Region.class), any(Pageable.class)))
                .thenReturn(slice);

        Long memberId = 1L;
        Pageable pageable = PageRequest.of(0, 2);

        //when
        MultiBoardSelectionResponse multiBoardSelectionResponse = boardService.selectRegionBoards(memberId, pageable);

        //then
        assertThat(multiBoardSelectionResponse.getBoards()).hasSize(2);
        verify(boardRepository, times(1)).selectRegionBoards(any(Region.class), any(Pageable.class));
    }

    @Test
    @DisplayName("요청 회원에 해당하는 지역에 대한 모집 중인 게시글을 활동 시작일 기준으로 오름차순으로 조회한 후, 조회한 게시글들에 대해" +
            "경도, 위도, 상세 주소, boardId를 MultiPlaceSelectionResponse 로 감싸 반환한다.")
    void selectRegionBoardsCloseToDeadline() {
        //given
        LocalDateTime now = LocalDateTime.of(2023, 3, 25, 1, 1);
        Member dongJackMember = new Member("test@mail.com", "동작이");
        dongJackMember.updateRegion("동작구");
        RecruitmentBoard dongJackBoard1 = new RecruitmentBoard(now, dongJackMember,
                LocalDate.of(2023, 3, 26), "달리기", createTestPlace(), "제목1", "본문1", 8);
        RecruitmentBoard dongJackBoard2 = new RecruitmentBoard(now, dongJackMember,
                LocalDate.of(2023, 3, 27), "산책", createTestPlace(), "제목2", "본문2", 8);
        Slice<RecruitmentBoard> slice = new SliceImpl<>(List.of(dongJackBoard1, dongJackBoard2));

        Member member = mock(Member.class);
        when(member.getRegion()).thenReturn(Region.from("동작구"));
        when(memberService.findByMemberId(1L)).thenReturn(member);
        when(boardRepository.selectRecruitmentRegionBoardsCloseToDeadline(any(Region.class), any(Pageable.class)))
                .thenReturn(slice);

        Long memberId = 1L;
        Pageable pageable = PageRequest.of(0, 2);

        //when
        MultiBoardPlaceSelectionResponse multiBoardPlaceSelectionResponse = boardService.selectRegionBoardsCloseToDeadline(memberId, pageable);

        //then
        assertThat(multiBoardPlaceSelectionResponse.getBoardPlaceMarkerResponses()).hasSize(2);
        verify(boardRepository, times(1)).selectRecruitmentRegionBoardsCloseToDeadline(any(Region.class), any(Pageable.class));
    }

    @Test
    @DisplayName("게시글에 참여 요청 시, 게시글의 참여리스트에 회원을 추가한다.")
    void participate() {
        //given
        Member member = mock(Member.class);
        when(memberService.findByMemberId(anyLong())).thenReturn(member);

        RecruitmentBoard board = mock(RecruitmentBoard.class);
        Long boardId = 2L;
        when(board.getId()).thenReturn(boardId);
        when(boardRepository.findByIdWithOptimisticLock(anyLong())).thenReturn(Optional.ofNullable(board));

        //when
        RecruitmentBoardIdResponse actual = boardService.participate(1L, 1L);

        //then
        assertThat(actual.getBoardId()).isEqualTo(boardId);
    }

    @Test
    @DisplayName("게시글에 참여 요청 시, 게시글의 모집이 마감되었으면 예외를 반환한다.")
    void failedByParticipateByClosedRecruitment() {
        //given
        Member member = mock(Member.class);
        when(memberService.findByMemberId(anyLong())).thenReturn(member);

        RecruitmentBoard board = mock(RecruitmentBoard.class);
        when(boardRepository.findByIdWithOptimisticLock(anyLong())).thenReturn(Optional.ofNullable(board));
        doThrow(new IllegalArgumentException("모집이 마감된 게시글 입니다.")).when(board).addParticipationMember(member);

        //when, then
        assertThatThrownBy(() -> boardService.participate(1L, 1L)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("모집이 마감된 게시글 입니다.");
    }

    @Test
    @DisplayName("게시글에 참여 요청 시, 게시글의 모집 인원이 꽉 찼으면 예외를 반환한다.")
    void failedByFullParticipationCount() {
        //given
        Member member = mock(Member.class);
        when(memberService.findByMemberId(anyLong())).thenReturn(member);

        RecruitmentBoard board = mock(RecruitmentBoard.class);
        when(boardRepository.findByIdWithOptimisticLock(anyLong())).thenReturn(Optional.ofNullable(board));
        doThrow(new IllegalStateException("참여 인원이 꽉 찼습니다.")).when(board).addParticipationMember(member);

        //when, then
        assertThatThrownBy(() -> boardService.participate(1L, 1L)).isInstanceOf(IllegalStateException.class)
                .hasMessage("참여 인원이 꽉 찼습니다.");
    }
}

