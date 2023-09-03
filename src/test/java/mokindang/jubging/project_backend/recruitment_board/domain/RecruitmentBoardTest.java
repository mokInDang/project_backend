package mokindang.jubging.project_backend.recruitment_board.domain;

import mokindang.jubging.project_backend.exception.custom.ForbiddenException;
import mokindang.jubging.project_backend.member.domain.Member;
import mokindang.jubging.project_backend.member.domain.vo.Region;
import mokindang.jubging.project_backend.recruitment_board.domain.ActivityCategory;
import mokindang.jubging.project_backend.recruitment_board.domain.RecruitmentBoard;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.ContentBody;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.StartingDate;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.Title;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.place.Coordinate;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.place.Place;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback
@DataJpaTest
class RecruitmentBoardTest {

    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("게시글 생성 시, 모집 여부는 상태는 항상 true 이다.")
    void defaultOfOnRecruitment() {
        //given
        RecruitmentBoard recruitmentBoard = findTestRecruitmentBoard();

        //when
        boolean onRecruitment = recruitmentBoard.isOnRecruitment();

        //then
        assertThat(onRecruitment).isTrue();
    }

    private RecruitmentBoard findTestRecruitmentBoard() {
        return em.find(RecruitmentBoard.class, 1L);
    }

    private Place createTestPlace() {
        Coordinate coordinate = new Coordinate(1.1, 1.2);
        return new Place(coordinate, "서울시 동작구 상도동 1-1");
    }

    @Test
    @DisplayName("회원 Id 를 받아 모집 여부를 마감한다.")
    void closeRecruitment() {
        //given
        RecruitmentBoard recruitmentBoard = findTestRecruitmentBoard();
        Long memberId = recruitmentBoard.getWriter()
                .getId();

        //when
        recruitmentBoard.closeRecruitment(memberId);

        //then
        assertThat(recruitmentBoard.isOnRecruitment()).isFalse();
    }

    @Test
    @DisplayName("회원 Id 를 받아 권환을 확인 후 모집 여부를 마감한다. 이때 작성자가 아닌 회원이면 예외를 반환한다.")
    void closeRecruitmentWhenNoneWriterId() {
        //given
        RecruitmentBoard recruitmentBoard = findTestRecruitmentBoard();
        Long memberId = recruitmentBoard.getWriter()
                .getId() + 1;

        //when, then
        assertThatThrownBy(() -> recruitmentBoard.closeRecruitment(memberId)).isInstanceOf(ForbiddenException.class)
                .hasMessage("작성자 권한이 없습니다.");
    }

    @Test
    @DisplayName("게시글의 member(작성자), title, content, 활동 종류, 활동 시작일, 모집 여부, 작성 지역, 만남장소 참여중인 인원을 반환한다.")
    void getter() {
        //given
        SoftAssertions softly = new SoftAssertions();
        LocalDateTime now = LocalDateTime.of(2023, 11, 12, 0, 0, 0);
        Member testMember = new Member("koho1047@naver.com", "민호");
        testMember.updateRegion("동작구");
        RecruitmentBoard recruitmentBoard = new RecruitmentBoard(now, testMember, LocalDate.of(2025, 2, 11),
                "달리기", createTestPlace(), "게시판 제목", "게시판 내용 작성 테스트", 8);

        //when
        Member member = recruitmentBoard.getWriter();
        Title title = recruitmentBoard.getTitle();
        ContentBody contentBody = recruitmentBoard.getContentBody();
        ActivityCategory activityCategory = recruitmentBoard.getActivityCategory();
        StartingDate startingDate = recruitmentBoard.getStartingDate();
        Region region = recruitmentBoard.getWritingRegion();
        boolean onRecruitment = recruitmentBoard.isOnRecruitment();
        int numberOfMemberParticipating = recruitmentBoard.getParticipationList().size();
        Place meetingPlace = recruitmentBoard.getMeetingPlace();

        //then
        softly.assertThat(member).isEqualTo(testMember);
        softly.assertThat(activityCategory).isSameAs(ActivityCategory.RUNNING);
        softly.assertThat(startingDate).isEqualTo(new StartingDate(LocalDate.of(2023, 11, 11),
                LocalDate.of(2025, 2, 11)));
        softly.assertThat(title).isEqualTo(new Title("게시판 제목"));
        softly.assertThat(contentBody).isEqualTo(new ContentBody("게시판 내용 작성 테스트"));
        softly.assertThat(region).isEqualTo(member.getRegion());
        softly.assertThat(onRecruitment).isEqualTo(true);
        softly.assertThat(meetingPlace).isEqualTo(createTestPlace());
        softly.assertThat(numberOfMemberParticipating).isEqualTo(1);
        softly.assertAll();
    }

    @Test
    @DisplayName("계시글 생성 시, 유저의 지역이 인증되지 않은 상태면 예외를 반환한다.")
    void validateRegion() {
        //given
        Member member = new Member("Test@email.com", "test");
        LocalDateTime now = LocalDateTime.of(2023, 11, 12, 0, 0, 0);

        //when, then
        assertThatThrownBy(() -> new RecruitmentBoard(now, member, LocalDate.of(2025, 2, 11),
                "달리기", createTestPlace(), "게시판 제목", "게시판 내용 작성 테스트", 8))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지역 인증이 되지 않아, 게시글을 생성할 수 없습니다.");
    }

    @Test
    @DisplayName("지역을 입력 받아, 게시글의 지역과 다르면 예외를 반환한다.")
    void checkRegion() {
        //given
        RecruitmentBoard recruitmentBoard = findTestRecruitmentBoard();

        //when, then
        assertThatThrownBy(() -> recruitmentBoard.checkRegion(Region.from("성동구")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지역이 다른 유저는 게시글에 접근 할 수 없습니다.");
    }

    @Test
    @DisplayName("게시글 작성자의 별명을 반환한다.")
    void getWriterAlias() {
        //given
        LocalDateTime now = LocalDateTime.of(2023, 11, 12, 0, 0, 0);
        Member testMember = new Member("koho1047@naver.com", "민호");
        testMember.updateRegion("동작구");
        Coordinate coordinate = new Coordinate(1.1, 1.2);
        RecruitmentBoard recruitmentBoard = new RecruitmentBoard(now, testMember, LocalDate.of(2025, 2, 11),
                "달리기", createTestPlace(), "게시판 제목", "게시판 내용 작성 테스트", 8);

        //when
        String actual = recruitmentBoard.getWriterAlias();

        //then
        assertThat(actual).isEqualTo("민호");
    }

    @Test
    @DisplayName("게시글 작성자의 이메일 앞 4글자를 반환한다.")
    void getFirstFourDigitsOfWriterEmail() {
        //given
        RecruitmentBoard recruitmentBoard = findTestRecruitmentBoard();
        String expect = "test";

        //when
        String actual = recruitmentBoard.getFirstFourDigitsOfWriterEmail();

        //then
        assertThat(actual).isEqualTo(expect);
    }

    @Test
    @DisplayName("회원 Id 를 입력받아 게시글 작성자인지 확인한다. 작성자인경우 true 를 반환한다.")
    void isSameWriterId() {
        //given
        RecruitmentBoard recruitmentBoard = findTestRecruitmentBoard();
        Member writer = recruitmentBoard.getWriter();

        Long writerId = writer.getId();

        //when
        boolean actual = recruitmentBoard.isSameWriterId(writerId);

        //then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("회원 ID 를 입력받아 게시글 작성자인지 확인한다. 작성자가 아닌경우 false 를 반환한다.")
    void isSameWriterIdFailed() {
        //given
        RecruitmentBoard recruitmentBoard = findTestRecruitmentBoard();
        long noneWriterId = recruitmentBoard.getWriter()
                .getId() + 1;

        //when
        boolean actual = recruitmentBoard.isSameWriterId(noneWriterId);

        //then
        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("회원 Id 를 입력받아, 게시글 작성자가 아닌경우 예외를 반환한다.")
    void validatePermission() {
        //given
        RecruitmentBoard recruitmentBoard = findTestRecruitmentBoard();
        Long noneWriterId = recruitmentBoard.getWriter()
                .getId() + 1;

        //when, then
        assertThatThrownBy(() -> recruitmentBoard.validatePermission(noneWriterId)).isInstanceOf(ForbiddenException.class)
                .hasMessage("작성자 권한이 없습니다.");
    }

    @Test
    @DisplayName("게시글 수정 요청 시, 활동 예정일, 활동 종류, 제목, 본문을 받아 변경을 한다.")
    void modify() {
        //given
        SoftAssertions softly = new SoftAssertions();

        RecruitmentBoard recruitmentBoard = findTestRecruitmentBoard();
        Long writerId = recruitmentBoard.getWriter()
                .getId();

        String newActivityCategory = "산책";
        String newTitleValue = "새로운 제목입니다.";
        String newContentValue = "새로운 본문 내용입니다.";
        LocalDate newStartingDate = LocalDate.parse("2023-11-13");
        Place place = new Place(new Coordinate(1.1, 1.2), "서울시 동작구 상도동 1-1");

        //when
        recruitmentBoard.modify(writerId, newStartingDate, newActivityCategory, newTitleValue, newContentValue, place);

        //then
        softly.assertThat(recruitmentBoard.getStartingDate().getValue()).isEqualTo("2023-11-13");
        softly.assertThat(recruitmentBoard.getActivityCategory().getValue()).isEqualTo(newActivityCategory);
        softly.assertThat(recruitmentBoard.getTitle().getValue()).isEqualTo(newTitleValue);
        softly.assertThat(recruitmentBoard.getContentBody().getValue()).isEqualTo(newContentValue);
        softly.assertThat(recruitmentBoard.getMeetingPlace()).isEqualTo(place);
        softly.assertAll();
    }

    @Test
    @DisplayName("게시글 수정 요청 시, 요청 회원의 id 가 작성자 id 가 아닌 경우 예외를 반환한다.")
    void modifyFailedWhenNoneWriterId() {
        //given
        RecruitmentBoard recruitmentBoard = findTestRecruitmentBoard();
        Long noneWriterId = 10L;
        String newActivityCategory = "산책";
        String newTitleValue = "새로운 제목입니다.";
        String newContentValue = "새로운 본문 내용입니다.";
        LocalDate newStartingDate = LocalDate.parse("2023-11-13");
        Place place = new Place(new Coordinate(1.1, 1.2), "서울시 동작구 상도동 1-1");

        //when, then
        assertThatThrownBy(() -> recruitmentBoard.modify(noneWriterId, newStartingDate, newActivityCategory, newTitleValue, newContentValue, place))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("작성자 권한이 없습니다.");
    }

    @Test
    @DisplayName("게시글 작성자의 프로필 이미지 url 을 불러온다.")
    void getWriterProfileImageUrl() {
        //given
        String testUrl = "test_url";

        LocalDateTime now = LocalDateTime.of(2023, 11, 12, 0, 0, 0);
        Member writer = new Member("test1@email.com", "test");
        writer.updateRegion("동작구");
        writer.updateProfileImage(testUrl);
        Coordinate coordinate = new Coordinate(1.1, 1.2);
        RecruitmentBoard recruitmentBoard = new RecruitmentBoard(now, writer, LocalDate.of(2025, 2, 11), "달리기",
                createTestPlace(), "제목", "본문내용", 8);

        //when
        String writerProfileImageUrl = recruitmentBoard.getWriterProfileImageUrl();

        //then
        assertThat(writerProfileImageUrl).isEqualTo(testUrl);
    }

    @Test
    @DisplayName("구인 게시글에 달린 모든 댓글과 대댓글의 갯수를 반환한다.")
    void countCommentAndReplyComment() {
        //given
        RecruitmentBoard recruitmentBoard = findTestRecruitmentBoard();

        //when
        Long actual = recruitmentBoard.countCommentAndReplyComment();

        //then
        assertThat(actual).isEqualTo(3);
    }


    @ParameterizedTest
    @CsvSource(value = {"성동구 , false", "동작구 , true"})
    @DisplayName("게시글 작성 지역에 대해서 입력 받은 지역이 같은지 확인한다.")
    void isSameRegion(final String input, final boolean expect) {
        //given
        RecruitmentBoard recruitmentBoard = findTestRecruitmentBoard();
        Region region = Region.from(input);

        //when
        boolean actual = recruitmentBoard.isSameRegion(region);

        //then
        assertThat(actual).isEqualTo(expect);
    }

    @Test
    @DisplayName("참여 회원 추가 시 참여 회원을 입력받아 참여인원수를 1 업한 후, 회원을 참여 리스트에 추가한다.")
    void addParticipationMember() {
        //given
        SoftAssertions softly = new SoftAssertions();
        RecruitmentBoard recruitmentBoard = findTestRecruitmentBoard();
        Member member = mock(Member.class);
        when(member.getId()).thenReturn(2L);
        when(member.getRegion()).thenReturn(Region.from("동작구"));

        //when
        recruitmentBoard.addParticipationMember(member);

        //then
        softly.assertThat(recruitmentBoard.getParticipationList().size()).isEqualTo(2);
        softly.assertThat(recruitmentBoard.getParticipationCount().getCount()).isEqualTo(2);
        softly.assertAll();
    }

    @Test
    @DisplayName("참여 회원 추가시 인원이 이미 찼다면 예외를 반환한다.")
    void failedByFullParticipationCount() {
        //given
        Member member = mock(Member.class);
        when(member.getId()).thenReturn(2L);
        when(member.getRegion()).thenReturn(Region.from("동작구"));

        int maxParticipationCount = 1;
        LocalDateTime now = LocalDateTime.of(2023, 11, 12, 0, 0, 0);
        Member writer = mock(Member.class);
        when(writer.getId()).thenReturn(1L);
        when(writer.getRegion()).thenReturn(Region.from("동작구"));

        RecruitmentBoard recruitmentBoard = new RecruitmentBoard(now, writer, LocalDate.of(2025, 2, 11), "달리기",
                createTestPlace(), "제목", "본문내용", maxParticipationCount);

        //when, then
        assertThatThrownBy(() -> recruitmentBoard.addParticipationMember(member)).isInstanceOf(IllegalStateException.class)
                .hasMessage("참여 인원이 꽉 찼습니다.");
    }

    @Test
    @DisplayName("참여 회원 추가 시, 모집이 마감 되었다면 예외를 반환한다.")
    void failedByClosedRecruitment() {
        //given
        RecruitmentBoard recruitmentBoard = findTestRecruitmentBoard();
        recruitmentBoard.closeRecruitment(1L);

        Member member = new Member("test1@email.com", "test");
        member.updateRegion("동작구");

        //when, then
        assertThatThrownBy(() -> recruitmentBoard.addParticipationMember(member)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("모집이 마감된 게시글 입니다.");
    }

    @Test
    @DisplayName("참여 회원 추가 시, 해당 회원의 지역이 게시글의 지역과 다르면 예외를 반환한다. ")
    void failedByOtherRegionMemberRecruitment() {
        //given
        RecruitmentBoard recruitmentBoard = findTestRecruitmentBoard();
        recruitmentBoard.closeRecruitment(1L);

        Member member = new Member("test1@email.com", "test");
        member.updateRegion("성동구");

        //when, then
        assertThatThrownBy(() -> recruitmentBoard.addParticipationMember(member)).isInstanceOf(ForbiddenException.class)
                .hasMessage("타지역 게시글에 참여할 권한이 없습니다.");
    }
}
