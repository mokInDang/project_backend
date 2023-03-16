package mokindang.jubging.project_backend.service.board;

import mokindang.jubging.project_backend.domain.board.Board;
import mokindang.jubging.project_backend.domain.member.Member;
import mokindang.jubging.project_backend.repository.board.BoardRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardSchedulingServiceTest {

    private static final String DIFFERENCE_BETWEEN_UCT_AND_KST = "+9";
    @Mock
    BoardRepository boardRepository;

    @Mock
    Clock clock;

    @InjectMocks
    private BoardSchedulingService boardSchedulingService;

    @Test
    @DisplayName("게시글 활동 여부가 true 인 것들 중, 활동 시작일이 현재 시간보다 과거에 있으면 활동 여부를 false 로 변경한다.")
    void updateOnRecruitmentByStartingDate() {
        //given
        SoftAssertions softly = new SoftAssertions();

        List<Board> boards = inputBoards();
        when(boardRepository.findBoardsByOnRecruitmentTrue()).thenReturn(boards);
        OffsetDateTime now = OffsetDateTime.of(LocalDateTime.of(2023, 3, 18, 0, 0), ZoneOffset.of(DIFFERENCE_BETWEEN_UCT_AND_KST));
        when(clock.instant()).thenReturn(now.toInstant());
        when(clock.getZone()).thenReturn(ZoneId.of("Asia/Seoul"));

        //when
        boardSchedulingService.updateOnRecruitmentByStartingDate();

        //then
        verify(boardRepository, times(1)).findBoardsByOnRecruitmentTrue();
        softly.assertThat(boards.get(0).isOnRecruitment()).isEqualTo(true);
        softly.assertThat(boards.get(1).isOnRecruitment()).isEqualTo(false);
        softly.assertThat(boards.get(2).isOnRecruitment()).isEqualTo(false);
        softly.assertAll();
    }

    private List<Board> inputBoards() {
        Member member = new Member("test@mail.com", "test");
        member.updateRegion("동작구");
        LocalDate dateOfCreation = LocalDate.of(2023, 3, 15);

        LocalDate StartingDateOfRecruitingBoard = LocalDate.of(2023, 3, 19);
        Board recruitingBoard = new Board(member, StartingDateOfRecruitingBoard, "달리기", "제목", "본문", dateOfCreation);

        LocalDate pastStartingDate = LocalDate.of(2023, 3, 17);
        Board noneRecruitingBoard = new Board(member, pastStartingDate, "달리기", "제목", "본문", dateOfCreation);
        noneRecruitingBoard.closeRecruitment();

        Board recruitingBoardWithPastStartingDate = new Board(member, pastStartingDate, "달리기", "제목", "본문", dateOfCreation);
        return List.of(recruitingBoard, noneRecruitingBoard, recruitingBoardWithPastStartingDate);
    }
}
