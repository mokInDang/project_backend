package mokindang.jubging.project_backend.service.board.response;

import mokindang.jubging.project_backend.domain.board.recruitment.ActivityCategory;
import mokindang.jubging.project_backend.domain.board.recruitment.RecruitmentBoard;
import mokindang.jubging.project_backend.domain.board.recruitment.vo.BoardContentBody;
import mokindang.jubging.project_backend.domain.board.recruitment.vo.StartingDate;
import mokindang.jubging.project_backend.domain.board.recruitment.vo.Title;
import mokindang.jubging.project_backend.domain.member.Member;
import mokindang.jubging.project_backend.domain.member.vo.Region;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BoardSelectionResponseTest {

    @Test
    @DisplayName("게시글과 게시글의 작성자 여부를 입력받아 BoardSelectionResponse 를 생성한다.")
    void create() {
        //given
        boolean mine = true;
        LocalDateTime now = LocalDateTime.of(2023, 11, 12, 0, 0, 0);
        Member writer = new Member("test1@email.com", "test");
        writer.updateRegion("동작구");
        RecruitmentBoard recruitmentBoard = new RecruitmentBoard(now, writer, LocalDate.of(2025, 2, 11), "달리기",
                "제목", "본문내용");

        //when, then
        assertThatCode(() -> new RecruitmentBoardSelectionResponse(recruitmentBoard, mine)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("게시글 조회에 대한 정보를 반환한다.")
    void getter() {
        //given
        SoftAssertions softly = new SoftAssertions();
        RecruitmentBoard recruitmentBoard = createMockedBoard();
        boolean mine = true;
        RecruitmentBoardSelectionResponse selectionResponse = new RecruitmentBoardSelectionResponse(recruitmentBoard, mine);

        //when
        Long boardId = selectionResponse.getBoardId();
        String activityCategory = selectionResponse.getActivityCategory();
        String region = selectionResponse.getRegion();
        String title = selectionResponse.getTitle();
        String content = selectionResponse.getContentBody();
        boolean onRecruitment = selectionResponse.isOnRecruitment();
        LocalDateTime creatingDatetime = selectionResponse.getCreatingDatetime();
        String startingDate = selectionResponse.getStartingDate();
        String writerAlias = selectionResponse.getWriterAlias();
        String firstFourLettersOfEmail = selectionResponse.getFirstFourLettersOfEmail();
        String writerProfileImageUrl = selectionResponse.getWriterProfileImageUrl();
        boolean isMine = selectionResponse.isMine();

        //then
        softly.assertThat(boardId).isEqualTo(1L);
        softly.assertThat(activityCategory).isEqualTo("달리기");
        softly.assertThat(region).isEqualTo("동작구");
        softly.assertThat(title).isEqualTo("제목");
        softly.assertThat(content).isEqualTo("본문내용");
        softly.assertThat(onRecruitment).isTrue();
        softly.assertThat(creatingDatetime).isEqualTo(LocalDateTime.of(2023, 11, 12, 0, 0, 0));
        softly.assertThat(startingDate).isEqualTo("2025-02-11");
        softly.assertThat(writerAlias).isEqualTo("test");
        softly.assertThat(firstFourLettersOfEmail).isEqualTo("test");
        softly.assertThat(writerProfileImageUrl).isEqualTo("test_url");
        softly.assertThat(isMine).isTrue();
        softly.assertAll();
    }

    private RecruitmentBoard createMockedBoard() {
        LocalDate today = LocalDate.of(2023, 3, 14);
        RecruitmentBoard recruitmentBoard = mock(RecruitmentBoard.class);
        when(recruitmentBoard.getId()).thenReturn(1L);
        when(recruitmentBoard.getTitle()).thenReturn(new Title("제목"));
        when(recruitmentBoard.getBoardContentBody()).thenReturn(new BoardContentBody("본문내용"));
        when(recruitmentBoard.getWritingRegion()).thenReturn(Region.from("동작구"));
        when(recruitmentBoard.getActivityCategory()).thenReturn(ActivityCategory.RUNNING);
        when(recruitmentBoard.isOnRecruitment()).thenReturn(true);
        when(recruitmentBoard.getCreatingDateTime()).thenReturn(LocalDateTime.of(2023, 11, 12, 0, 0, 0));
        when(recruitmentBoard.getStartingDate()).thenReturn(new StartingDate(today, LocalDate.of(2025, 2, 11)));
        when(recruitmentBoard.getWriterAlias()).thenReturn("test");
        when(recruitmentBoard.getFirstFourDigitsOfWriterEmail()).thenReturn("test");
        when(recruitmentBoard.getWriterProfileImageUrl()).thenReturn("test_url");
        return recruitmentBoard;
    }
}
