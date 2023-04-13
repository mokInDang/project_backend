package mokindang.jubging.project_backend.service.board.response;

import mokindang.jubging.project_backend.domain.board.Board;
import mokindang.jubging.project_backend.domain.member.Member;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

class BoardSelectionResponseTest {

    @Test
    @DisplayName("게시글과 게시글의 작성자 여부를 입력받아 BoardSelectionResponse 를 생성한다.")
    void create() {
        //given
        boolean mine = true;
        LocalDateTime now = LocalDateTime.of(2023, 11, 12, 0, 0, 0);
        Member writer = new Member("test1@email.com", "test");
        writer.updateRegion("동작구");
        Board board = new Board(now, writer, LocalDate.of(2025, 2, 11), "달리기",
                "제목", "본문내용");

        //when, then
        assertThatCode(() -> new BoardSelectionResponse(board, mine)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("게시글 조회에 대한 정보를 반환한다.")
    void getter() {
        //given
        SoftAssertions softly = new SoftAssertions();
        Board board = createBoard();
        boolean mine = true;
        BoardSelectionResponse boardSelectionResponse = new BoardSelectionResponse(board, mine);

        //when
        Long boardId = boardSelectionResponse.getBoardId();
        String activityCategory = boardSelectionResponse.getActivityCategory();
        String region = boardSelectionResponse.getRegion();
        String title = boardSelectionResponse.getTitle();
        String content = boardSelectionResponse.getContent();
        boolean onRecruitment = boardSelectionResponse.isOnRecruitment();
        LocalDateTime creatingDatetime = boardSelectionResponse.getCreatingDatetime();
        String startingDate = boardSelectionResponse.getStartingDate();
        String writerAlias = boardSelectionResponse.getWriterAlias();
        String firstFourLettersOfEmail = boardSelectionResponse.getFirstFourLettersOfEmail();
        String writerProfileImageUrl = boardSelectionResponse.getWriterProfileImageUrl();
        boolean isMine = boardSelectionResponse.isMine();

        //then
        softly.assertThat(boardId).isEqualTo(null);
        softly.assertThat(activityCategory).isEqualTo("달리기");
        softly.assertThat(region).isEqualTo("동작구");
        softly.assertThat(title).isEqualTo("제목");
        softly.assertThat(content).isEqualTo("본문내용");
        softly.assertThat(onRecruitment).isTrue();
        softly.assertThat(creatingDatetime).isEqualTo(LocalDateTime.of(2023,11,12,0,0,0));
        softly.assertThat(startingDate).isEqualTo("2025-02-11");
        softly.assertThat(writerAlias).isEqualTo("test");
        softly.assertThat(firstFourLettersOfEmail).isEqualTo("test");
        softly.assertThat(writerProfileImageUrl).isEqualTo("test_url");
        softly.assertThat(isMine).isTrue();
        softly.assertAll();
    }

    private Board createBoard() {
        LocalDateTime now = LocalDateTime.of(2023, 11, 12, 0, 0, 0);
        Member writer = new Member("test1@email.com", "test");
        writer.updateProfileImage("test_url","image");
        writer.updateRegion("동작구");
        return new Board(now, writer, LocalDate.of(2025, 2, 11), "달리기",
                "제목", "본문내용");
    }
}
