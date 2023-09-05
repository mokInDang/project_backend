package mokindang.jubging.project_backend.recruitment_board.service.response;

import mokindang.jubging.project_backend.member.MockedMemberFactory;
import mokindang.jubging.project_backend.member.domain.Member;
import mokindang.jubging.project_backend.member.domain.vo.ProfileImage;
import mokindang.jubging.project_backend.member.domain.vo.Region;
import mokindang.jubging.project_backend.recruitment_board.domain.ActivityCategory;
import mokindang.jubging.project_backend.recruitment_board.domain.RecruitmentBoard;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.ContentBody;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.ParticipationCount;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.StartingDate;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.Title;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.place.Coordinate;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.place.Place;
import mokindang.jubging.project_backend.recruitment_board.service.MockedRecruitmentBoardFactory;
import mokindang.jubging.project_backend.recruitment_board.service.response.board.RecruitmentBoardSelectionResponse;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static mokindang.jubging.project_backend.member.MockedMemberFactory.createMockedMember;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BoardSelectionResponseTest {

    @Test
    @DisplayName("게시글과  작성자를 입력받아 BoardSelectionResponse 를 생성한다.")
    void create() {
        //given
        Member member = createMockedMember(1L);
        LocalDateTime now = LocalDateTime.of(2023, 11, 12, 0, 0, 0);
        Member writer = createMockedMember(1L);
        RecruitmentBoard recruitmentBoard = new RecruitmentBoard(now, writer, LocalDate.of(2025, 2, 11), "달리기",
                createTestPlace(), "제목", "본문내용", 8);

        //when, then
        assertThatCode(() -> new RecruitmentBoardSelectionResponse(member, recruitmentBoard)).doesNotThrowAnyException();
    }

    private Place createTestPlace() {
        Coordinate coordinate = new Coordinate(1.1, 1.2);
        return new Place(coordinate, "서울시 동작구 상도동 1-1");
    }

    @Test
    @DisplayName("게시글 조회에 대한 정보를 반환한다.")
    void getter() {
        //given
        SoftAssertions softly = new SoftAssertions();
        RecruitmentBoard recruitmentBoard = MockedRecruitmentBoardFactory.createMockedRecruitmentBoard(1L);
        Member member = createMockedMember(1L);
        RecruitmentBoardSelectionResponse selectionResponse = new RecruitmentBoardSelectionResponse(member, recruitmentBoard);

        //when
        Long boardId = selectionResponse.getBoardId();
        String activityCategory = selectionResponse.getActivityCategory();
        String region = selectionResponse.getRegion();
        String title = selectionResponse.getTitle();
        String contentBody = selectionResponse.getContentBody();
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
        softly.assertThat(contentBody).isEqualTo("본문내용");
        softly.assertThat(onRecruitment).isTrue();
        softly.assertThat(creatingDatetime).isEqualTo(LocalDateTime.of(2023, 11, 12, 0, 0, 0));
        softly.assertThat(startingDate).isEqualTo("2025-02-11");
        softly.assertThat(writerAlias).isEqualTo("test");
        softly.assertThat(firstFourLettersOfEmail).isEqualTo("test");
        softly.assertThat(writerProfileImageUrl).isEqualTo("test_url");
        softly.assertThat(isMine).isTrue();
        softly.assertAll();
    }
}
