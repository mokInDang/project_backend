package mokindang.jubging.project_backend.service.board;

import mokindang.jubging.project_backend.domain.board.ActivityCategory;
import mokindang.jubging.project_backend.domain.board.Board;
import mokindang.jubging.project_backend.domain.board.vo.Content;
import mokindang.jubging.project_backend.domain.board.vo.StartingDate;
import mokindang.jubging.project_backend.domain.board.vo.Title;
import mokindang.jubging.project_backend.domain.member.Member;
import mokindang.jubging.project_backend.domain.region.vo.Region;
import mokindang.jubging.project_backend.repository.board.BoardRepository;
import mokindang.jubging.project_backend.service.board.request.BoardCreateRequest;
import mokindang.jubging.project_backend.service.board.response.BoardSelectResponse;
import mokindang.jubging.project_backend.service.member.MemberService;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @Mock
    private MemberService memberService;

    @Mock
    private BoardRepository boardRepository;

    @InjectMocks
    private BoardService boardService;

    @Test
    @DisplayName("해당하는 유저가 작성한 게시글을 저장한다.")
    void write() {
        //given
        Member member = mock(Member.class);
        when(memberService.findByMemberId(anyLong())).thenReturn(member);

        BoardCreateRequest boardCreateRequest = new BoardCreateRequest("제목", "본문내용", "달리기",
                LocalDate.of(2025, 2, 12), LocalDate.of(2023, 11, 10));

        //when
        boardService.write(1L, boardCreateRequest);

        //then
        verify(boardRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 유저가 게시글 저장 시, 예외를 발생한다.")
    void writeFailedByNonexistentMember() {
        //given
        when(memberService.findByMemberId(anyLong())).thenThrow(new IllegalArgumentException("해당하는 유저가 존재하지 않습니다."));

        BoardCreateRequest boardCreateRequest = new BoardCreateRequest("제목", "본문내용", "달리기",
                LocalDate.of(2023, 2, 12), LocalDate.of(2023, 11, 10));

        //when, then
        assertThatThrownBy(() -> boardService.write(1L, boardCreateRequest))
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

        Board board = mock(Board.class);
        when(board.getId()).thenReturn(1L);
        when(board.getTitle()).thenReturn(new Title("제목입니다."));
        when(board.getContent()).thenReturn(new Content("본문내용입니다."));
        when(board.getWriter()).thenReturn(mock(Member.class));
        when(board.getWriter().getAlias()).thenReturn("글작성자");
        when(board.getRegion()).thenReturn(Region.from("동작구"));
        when(board.getActivityCategory()).thenReturn(ActivityCategory.RUNNING);
        LocalDate now = LocalDate.of(2023, 3, 10);
        when(board.getStartingDate()).thenReturn(new StartingDate(now, LocalDate.of(2023, 3, 11)));
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
        when(member.getRegion()).thenReturn(Region.from("동작구"));

        //when
        BoardSelectResponse actual = boardService.select(1L, 1L);

        //then
        softly.assertThat(actual.getBoardId()).isEqualTo(1L);
        softly.assertThat(actual.getTitle()).isEqualTo("제목");
        softly.assertThat(actual.getWriterAlias()).isEqualTo("test");
    }
}
