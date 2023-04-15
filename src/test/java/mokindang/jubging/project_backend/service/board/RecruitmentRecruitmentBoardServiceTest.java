package mokindang.jubging.project_backend.service.board;

import mokindang.jubging.project_backend.domain.board.recruitment.ActivityCategory;
import mokindang.jubging.project_backend.domain.board.recruitment.RecruitmentBoard;
import mokindang.jubging.project_backend.domain.board.recruitment.vo.Content;
import mokindang.jubging.project_backend.domain.board.recruitment.vo.StartingDate;
import mokindang.jubging.project_backend.domain.board.recruitment.vo.Title;
import mokindang.jubging.project_backend.domain.member.Member;
import mokindang.jubging.project_backend.domain.member.vo.Region;
import mokindang.jubging.project_backend.exception.custom.ForbiddenException;
import mokindang.jubging.project_backend.repository.board.RecruitmentBoardRepository;
import mokindang.jubging.project_backend.service.board.request.RecruitmentBoardCreationRequest;
import mokindang.jubging.project_backend.service.board.request.BoardModificationRequest;
import mokindang.jubging.project_backend.service.board.response.RecruitmentBoardIdResponse;
import mokindang.jubging.project_backend.service.board.response.RecruitmentBoardSelectionResponse;
import mokindang.jubging.project_backend.service.board.response.MultiBoardSelectResponse;
import mokindang.jubging.project_backend.service.member.MemberService;
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
class RecruitmentRecruitmentBoardServiceTest {

    @Mock
    private MemberService memberService;

    @Mock
    private RecruitmentBoardRepository recruitmentBoardRepository;

    @InjectMocks
    private RecruitmentBoardService recruitmentBoardService;

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
        RecruitmentBoard savedRecruitmentBoard = mock(RecruitmentBoard.class);
        when(savedRecruitmentBoard.getId()).thenReturn(1L);
        when(recruitmentBoardRepository.save(any(RecruitmentBoard.class))).thenReturn(savedRecruitmentBoard);

        RecruitmentBoardCreationRequest recruitmentBoardCreationRequest = new RecruitmentBoardCreationRequest("제목", "본문내용", "달리기",
                LocalDate.of(2025, 2, 12));

        //when
        RecruitmentBoardIdResponse savedBoardId = recruitmentBoardService.write(1L, recruitmentBoardCreationRequest);

        //then
        assertThat(savedBoardId.getBoardId()).isEqualTo(1L);
        verify(recruitmentBoardRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 유저가 게시글 저장 시, 예외를 발생한다.")
    void writeFailedByNonexistentMember() {
        //given
        when(memberService.findByMemberId(anyLong())).thenThrow(new IllegalArgumentException("해당하는 유저가 존재하지 않습니다."));

        RecruitmentBoardCreationRequest recruitmentBoardCreationRequest = new RecruitmentBoardCreationRequest("제목", "본문내용", "달리기",
                LocalDate.of(2023, 2, 12));

        //when, then
        assertThatThrownBy(() -> recruitmentBoardService.write(1L, recruitmentBoardCreationRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당하는 유저가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("게시글을 조회한다.")
    void selectBoardId() {
        //given
        SoftAssertions softly = new SoftAssertions();
        Member member = mock(Member.class);
        when(memberService.findByMemberId(anyLong())).thenReturn(member);

        RecruitmentBoard recruitmentBoard = mock(RecruitmentBoard.class);
        when(recruitmentBoard.getId()).thenReturn(1L);
        when(recruitmentBoard.getTitle()).thenReturn(new Title("제목입니다."));
        when(recruitmentBoard.getContent()).thenReturn(new Content("본문내용입니다."));
        when(recruitmentBoard.getWriter()).thenReturn(mock(Member.class));
        when(recruitmentBoard.getWriter().getAlias()).thenReturn("글작성자");
        when(recruitmentBoard.getWritingRegion()).thenReturn(Region.from("동작구"));
        when(recruitmentBoard.getActivityCategory()).thenReturn(ActivityCategory.RUNNING);
        when(recruitmentBoard.isOnRecruitment()).thenReturn(true);
        LocalDate now = LocalDate.of(2023, 3, 10);
        when(recruitmentBoard.getStartingDate()).thenReturn(new StartingDate(now, LocalDate.of(2023, 3, 11)));
        when(recruitmentBoard.getWriter().getFourLengthEmail()).thenReturn("test");
        when(recruitmentBoard.getWriterProfileImageUrl()).thenReturn("test_url");
        when(recruitmentBoard.isWriter(member)).thenReturn(true);
        when(recruitmentBoardRepository.findById(1L)).thenReturn(Optional.of(recruitmentBoard));

        //when
        RecruitmentBoardSelectionResponse actual = recruitmentBoardService.select(1L, 1L);

        //then
        softly.assertThat(actual.getBoardId()).isEqualTo(1L);
        softly.assertThat(actual.getTitle()).isEqualTo("제목입니다.");
        softly.assertThat(actual.getContent()).isEqualTo("본문내용입니다.");
        softly.assertThat(actual.getWriterAlias()).isEqualTo("글작성자");
        softly.assertThat(actual.getStartingDate()).isEqualTo("2023-03-11");
        softly.assertThat(actual.getActivityCategory()).isEqualTo("달리기");
        softly.assertThat(actual.isOnRecruitment()).isEqualTo(true);
        softly.assertThat(actual.getWriterProfileImage()).isEqualTo("test_url");
        softly.assertThat(actual.getFirstFourLettersOfEmail()).isEqualTo("test");
        softly.assertThat(actual.isMine()).isEqualTo(true);
        softly.assertAll();
    }

    @Test
    @DisplayName("게시글 작성자가 게시글 삭제를 요청한 경우 게시글을 삭제한다." +
            " 삭제 후 삭제된 게시글 Id를 가진 BoardIdResponse 를 반환한다.")
    void delete() {
        //given
        Member writer = mock(Member.class);
        when(memberService.findByMemberId(1L)).thenReturn(writer);
        RecruitmentBoard recruitmentBoard = mock(RecruitmentBoard.class);
        when(recruitmentBoard.isWriter(writer)).thenReturn(true);
        Long boardId = 1L;
        when(recruitmentBoardRepository.findById(boardId)).thenReturn(Optional.of(recruitmentBoard));
        when(recruitmentBoard.getId()).thenReturn(boardId);

        Long writerId = 1L;
        //when
        RecruitmentBoardIdResponse deleteRecruitmentBoardIdResponse = recruitmentBoardService.delete(writerId, boardId);

        //then
        assertThat(deleteRecruitmentBoardIdResponse.getBoardId()).isEqualTo(boardId);
        verify(recruitmentBoardRepository, times(1)).delete(any());
    }

    @Test
    @DisplayName("게시글 삭제 요청 시, 존재하지 않는 게시물에 대한 접근이면 예외를 발생한다.")
    void deleteFailedByNonexistentBoard() {
        //given
        Member writer = mock(Member.class);
        when(memberService.findByMemberId(1L)).thenReturn(writer);
        when(recruitmentBoardRepository.findById(anyLong())).thenThrow(new IllegalArgumentException("존재하지 않는 게시물 입니다."));

        //when, then
        assertThatThrownBy(() -> recruitmentBoardService.delete(1L, 1L)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 게시물 입니다.");
    }

    @Test
    @DisplayName("게시글 삭제 요청 시, 삭제 요청 회원이 게시글 작성자가 아닌 경우 예외를 반환한다.")
    void deleteFailedByNoneMatchingBoardWhitWritingMember() {
        //given
        Member member = mock(Member.class);
        when(memberService.findByMemberId(1L)).thenReturn(member);
        RecruitmentBoard recruitmentBoard = mock(RecruitmentBoard.class);
        when(recruitmentBoard.isWriter(any(Member.class))).thenReturn(false);
        when(recruitmentBoardRepository.findById(anyLong())).thenReturn(Optional.of(recruitmentBoard));

        //when, then
        assertThatThrownBy(() -> recruitmentBoardService.delete(1L, 1L)).isInstanceOf(ForbiddenException.class)
                .hasMessage("글 작성자만 게시글을 삭제할 수 있습니다.");
    }

    @Test
    @DisplayName("게시글을 수정한다.")
    void modify() {
        //given
        Member member = mock(Member.class);
        when(memberService.findByMemberId(anyLong())).thenReturn(member);
        RecruitmentBoard recruitmentBoard = mock(RecruitmentBoard.class);
        when(recruitmentBoard.getId()).thenReturn(1L);
        when(recruitmentBoard.isWriter(any(Member.class))).thenReturn(true);
        when(recruitmentBoardRepository.findById(anyLong())).thenReturn(Optional.of(recruitmentBoard));

        Long memberId = 1L;
        Long boardId = 1L;
        BoardModificationRequest boardModificationRequest = new BoardModificationRequest("변경 제목", "변경 본문",
                "산책", LocalDate.of(2023, 12, 12));

        //when
        RecruitmentBoardIdResponse actual = recruitmentBoardService.modify(memberId, boardId, boardModificationRequest);

        //then
        assertThat(actual.getBoardId()).isEqualTo(1L);
        verify(recruitmentBoard, times(1)).modify(any(), any(), any(), any());
    }

    @Test
    @DisplayName("게시글의 모집을 마감한다.")
    void closeRecruitment() {
        //given
        Member member = mock(Member.class);
        when(memberService.findByMemberId(anyLong())).thenReturn(member);
        RecruitmentBoard recruitmentBoard = mock(RecruitmentBoard.class);
        when(recruitmentBoard.getId()).thenReturn(1L);
        when(recruitmentBoard.isWriter(any(Member.class))).thenReturn(true);
        when(recruitmentBoardRepository.findById(anyLong())).thenReturn(Optional.of(recruitmentBoard));

        Long memberId = 1L;
        Long boardId = 1L;

        //when
        RecruitmentBoardIdResponse recruitmentBoardIdResponse = recruitmentBoardService.closeRecruitment(memberId, boardId);

        //then
        assertThat(recruitmentBoardIdResponse.getBoardId()).isEqualTo(1L);
        verify(recruitmentBoard, times(1)).closeRecruitment();
    }

    @Test
    @DisplayName("게시글 모집 마감 요청 시, 입력 받은 memberId 를 가진 회원이 작성한 게시글이 아닌 경우 예외를 반환한다.")
    void closeRecruitmentFailedByNoneMatchingBoardWhitWritingMember() {
        //given
        Member member = mock(Member.class);
        when(memberService.findByMemberId(anyLong())).thenReturn(member);
        RecruitmentBoard recruitmentBoard = mock(RecruitmentBoard.class);
        when(recruitmentBoardRepository.findById(anyLong())).thenReturn(Optional.of(recruitmentBoard));
        when(recruitmentBoard.isWriter(any(Member.class))).thenReturn(false);

        Long memberId = 1L;
        Long boardId = 1L;

        //when, then
        assertThatThrownBy(() -> recruitmentBoardService.closeRecruitment(memberId, boardId)).isInstanceOf(ForbiddenException.class)
                .hasMessage("글 작성자만 모집 마감할 수 있습니다.");
    }

    @Test
    @DisplayName("게시글 모집 마감 요청 시, 입력 받은 boardId 를 가진 게시글이 존재하지 않는다면 예외를 반환한다.")
    void closeRecruitmentFailedByNoneExistBoard() {
        //given
        Member member = mock(Member.class);
        when(memberService.findByMemberId(anyLong())).thenReturn(member);
        RecruitmentBoard recruitmentBoard = mock(RecruitmentBoard.class);
        when(recruitmentBoardRepository.findById(anyLong())).thenReturn(Optional.empty());

        Long memberId = 1L;
        Long boardId = 1L;

        assertThatThrownBy(() -> recruitmentBoardService.closeRecruitment(memberId, boardId)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 게시물입니다.");
    }

    @Test
    @DisplayName("요청 회원의 지역에 해당하는 게시글 리스트를 반환한다.")
    void selectRegionBoards() {
        //given
        LocalDateTime now = LocalDateTime.of(2023, 3, 25, 1, 1);
        Member dongJackMember = new Member("test@mail.com", "동작이");
        dongJackMember.updateRegion("동작구");
        RecruitmentBoard dongJackRecruitmentBoard1 = new RecruitmentBoard(now.plusDays(1), dongJackMember, LocalDate.of(2023, 3, 27), "달리기", "제목1", "본문1");
        RecruitmentBoard dongJackRecruitmentBoard2 = new RecruitmentBoard(now, dongJackMember, LocalDate.of(2023, 3, 27), "산책", "제목2", "본문2");
        Slice<RecruitmentBoard> slice = new SliceImpl<>(List.of(dongJackRecruitmentBoard1, dongJackRecruitmentBoard2));

        Member member = mock(Member.class);
        when(member.getRegion()).thenReturn(Region.from("동작구"));
        when(memberService.findByMemberId(1L)).thenReturn(member);
        when(recruitmentBoardRepository.selectRegionBoards(any(Region.class), any(Pageable.class)))
                .thenReturn(slice);

        Long memberId = 1L;
        Pageable pageable = PageRequest.of(0, 2);

        //when
        MultiBoardSelectResponse multiBoardSelectResponse = recruitmentBoardService.selectRegionBoards(1L, pageable);

        //then
        assertThat(multiBoardSelectResponse.getBoards()).hasSize(2);
        verify(recruitmentBoardRepository, times(1)).selectRegionBoards(any(Region.class), any(Pageable.class));
    }
}
