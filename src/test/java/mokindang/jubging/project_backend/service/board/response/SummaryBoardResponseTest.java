package mokindang.jubging.project_backend.service.board.response;

import mokindang.jubging.project_backend.domain.board.recruitment.ActivityCategory;
import mokindang.jubging.project_backend.domain.board.recruitment.RecruitmentBoard;
import mokindang.jubging.project_backend.domain.board.recruitment.vo.Content;
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

class SummaryBoardResponseTest {

    @Test
    @DisplayName("Board 객체를 입력받아 SummaryBoardResponse 를 생성한다.")
    void create() {
        //given
        RecruitmentBoard recruitmentBoard = createRecruitmentBoard();

        //when, then
        assertThatCode(() -> new SummaryBoardResponse(recruitmentBoard)).doesNotThrowAnyException();
    }

    private RecruitmentBoard createRecruitmentBoard() {
        LocalDateTime now = LocalDateTime.of(2023, 11, 12, 0, 0, 0);
        Member writer = new Member("test1@email.com", "test");
        writer.updateProfileImage("test_url", "image");
        writer.updateRegion("동작구");
        return new RecruitmentBoard(now, writer, LocalDate.of(2025, 2, 11), "달리기",
                "제목", "본문내용");
    }

    @Test
    @DisplayName("Board 게시글 중 요약 된 게시글에서 필요한 내용들을 반환한다.")
    void getter() {
        //given
        SoftAssertions softly = new SoftAssertions();
        RecruitmentBoard recruitmentBoard = createMockedBoard();
        SummaryBoardResponse summaryBoardResponse = new SummaryBoardResponse(recruitmentBoard);

        //when
        Long boardId = summaryBoardResponse.getBoardId();
        String title = summaryBoardResponse.getTitle();
        String content = summaryBoardResponse.getContent();
        String startingDate = summaryBoardResponse.getStartingDate();
        boolean onRecruitment = summaryBoardResponse.isOnRecruitment();
        String activityCategory = summaryBoardResponse.getActivityCategory();
        String region = summaryBoardResponse.getRegion();
        String writerAlias = summaryBoardResponse.getWriterAlias();
        String writerProfileUrl = summaryBoardResponse.getWriterProfileUrl();
        String firstFourLettersOfEmail = summaryBoardResponse.getFirstFourLettersOfEmail();

        //then
        softly.assertThat(activityCategory).isEqualTo("달리기");
        softly.assertThat(region).isEqualTo("동작구");
        softly.assertThat(title).isEqualTo("제목");
        softly.assertThat(content).isEqualTo("본문내용");
        softly.assertThat(onRecruitment).isTrue();
        softly.assertThat(startingDate).isEqualTo("2025-02-11");
        softly.assertThat(writerAlias).isEqualTo("test");
        softly.assertThat(firstFourLettersOfEmail).isEqualTo("test");
        softly.assertAll();
    }

    private RecruitmentBoard createMockedBoard() {
        LocalDate today = LocalDate.of(2023, 3, 14);
        RecruitmentBoard recruitmentBoard = mock(RecruitmentBoard.class);
        when(recruitmentBoard.getId()).thenReturn(1L);
        when(recruitmentBoard.getTitle()).thenReturn(new Title("제목"));
        when(recruitmentBoard.getContent()).thenReturn(new Content("본문내용"));
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