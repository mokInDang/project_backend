package mokindang.jubging.project_backend.domain.board.recruitment;

import mokindang.jubging.project_backend.domain.board.recruitment.ActivityCategory;
import mokindang.jubging.project_backend.domain.board.recruitment.RecruitmentBoard;
import mokindang.jubging.project_backend.domain.board.recruitment.vo.BoardContentBody;
import mokindang.jubging.project_backend.domain.board.recruitment.vo.StartingDate;
import mokindang.jubging.project_backend.domain.board.recruitment.vo.Title;
import mokindang.jubging.project_backend.domain.member.Member;
import mokindang.jubging.project_backend.domain.member.vo.Region;
import mokindang.jubging.project_backend.exception.custom.ForbiddenException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

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
        RecruitmentBoard recruitmentBoard = createRecruitmentBoardWithTestWriter();

        //when
        boolean onRecruitment = recruitmentBoard.isOnRecruitment();

        //then
        assertThat(onRecruitment).isTrue();
    }

    private RecruitmentBoard createRecruitmentBoardWithTestWriter() {
        LocalDateTime now = LocalDateTime.of(2023, 11, 12, 0, 0, 0);
        Member writer = new Member("test1@email.com", "test");
        writer.updateRegion("동작구");
        RecruitmentBoard recruitmentBoard = new RecruitmentBoard(now, writer, LocalDate.of(2025, 2, 11), "달리기",
                "제목", "본문내용");

        em.persist(writer);
        em.persist(recruitmentBoard);
        em.flush();
        em.clear();

        return recruitmentBoard;
    }

    @Test
    @DisplayName("회원 Id 를 받아 모집 여부를 마감한다.")
    void closeRecruitment() {
        //given
        RecruitmentBoard recruitmentBoard = createRecruitmentBoardWithTestWriter();
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
        RecruitmentBoard recruitmentBoard = createRecruitmentBoardWithTestWriter();
        Long memberId = recruitmentBoard.getWriter()
                .getId() + 1;

        //when, then
        assertThatThrownBy(() -> recruitmentBoard.closeRecruitment(memberId)).isInstanceOf(ForbiddenException.class)
                .hasMessage("작성자 권한이 없습니다.");
    }

    @Test
    @DisplayName("게시글의 member(작성자), title, content, 활동 종류, 활동 시작일, 모집 여부, 작성 지역을 반환한다.")
    void getter() {
        //given
        SoftAssertions softly = new SoftAssertions();
        LocalDateTime now = LocalDateTime.of(2023, 11, 12, 0, 0, 0);
        Member testMember = new Member("koho1047@naver.com", "민호");
        testMember.updateRegion("동작구");
        RecruitmentBoard recruitmentBoard = new RecruitmentBoard(now, testMember, LocalDate.of(2025, 2, 11),
                "달리기", "게시판 제목", "게시판 내용 작성 테스트");

        //when
        Member member = recruitmentBoard.getWriter();
        Title title = recruitmentBoard.getTitle();
        BoardContentBody boardContentBody = recruitmentBoard.getBoardContentBody();
        ActivityCategory activityCategory = recruitmentBoard.getActivityCategory();
        StartingDate startingDate = recruitmentBoard.getStartingDate();
        Region region = recruitmentBoard.getWritingRegion();
        boolean onRecruitment = recruitmentBoard.isOnRecruitment();

        //then
        softly.assertThat(member).isEqualTo(testMember);
        softly.assertThat(activityCategory).isSameAs(ActivityCategory.RUNNING);
        softly.assertThat(startingDate).isEqualTo(new StartingDate(LocalDate.of(2023, 11, 11),
                LocalDate.of(2025, 2, 11)));
        softly.assertThat(title).isEqualTo(new Title("게시판 제목"));
        softly.assertThat(boardContentBody).isEqualTo(new BoardContentBody("게시판 내용 작성 테스트"));
        softly.assertThat(region).isEqualTo(member.getRegion());
        softly.assertThat(onRecruitment).isEqualTo(true);
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
                "달리기", "게시판 제목", "게시판 내용 작성 테스트"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지역 인증이 되지 않아, 게시글을 생성할 수 없습니다.");
    }

    @Test
    @DisplayName("지역을 입력 받아, 게시글의 지역과 다르면 예외를 반환한다.")
    void checkRegion() {
        //given
        RecruitmentBoard recruitmentBoard = createRecruitmentBoardWithTestWriter();

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
        RecruitmentBoard recruitmentBoard = new RecruitmentBoard(now, testMember, LocalDate.of(2025, 2, 11),
                "달리기", "게시판 제목", "게시판 내용 작성 테스트");

        //when
        String actual = recruitmentBoard.getWriterAlias();

        //then
        assertThat(actual).isEqualTo("민호");
    }

    @Test
    @DisplayName("게시글 작성자의 이메일 앞 4글자를 반환한다.")
    void getFirstFourDigitsOfWriterEmail() {
        //given
        RecruitmentBoard recruitmentBoard = createRecruitmentBoardWithTestWriter();
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
        RecruitmentBoard recruitmentBoard = createRecruitmentBoardWithTestWriter();
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
        RecruitmentBoard recruitmentBoard = createRecruitmentBoardWithTestWriter();
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
        RecruitmentBoard recruitmentBoard = createRecruitmentBoardWithTestWriter();
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

        RecruitmentBoard recruitmentBoard = createRecruitmentBoardWithTestWriter();
        Long writerId = recruitmentBoard.getWriter()
                .getId();

        String newActivityCategory = "산책";
        String newTitleValue = "새로운 제목입니다.";
        String newContentValue = "새로운 본문 내용입니다.";
        LocalDate newStartingDate = LocalDate.parse("2023-11-13");


        //when
        recruitmentBoard.modify(writerId, newStartingDate, newActivityCategory, newTitleValue, newContentValue);

        //then
        softly.assertThat(recruitmentBoard.getStartingDate().getValue()).isEqualTo("2023-11-13");
        softly.assertThat(recruitmentBoard.getActivityCategory().getValue()).isEqualTo(newActivityCategory);
        softly.assertThat(recruitmentBoard.getTitle().getValue()).isEqualTo(newTitleValue);
        softly.assertThat(recruitmentBoard.getBoardContentBody().getValue()).isEqualTo(newContentValue);
        softly.assertAll();
    }

    @Test
    @DisplayName("게시글 수정 요청 시, 요청 회원의 id 가 작성자 id 가 아닌 경우 예외를 반환한다.")
    void modifyFailedWhenNoneWriterId() {
        //given
        RecruitmentBoard recruitmentBoard = createRecruitmentBoardWithTestWriter();
        Long noneWriterId = 10L;
        String newActivityCategory = "산책";
        String newTitleValue = "새로운 제목입니다.";
        String newContentValue = "새로운 본문 내용입니다.";
        LocalDate newStartingDate = LocalDate.parse("2023-11-13");

        //when, then
        assertThatThrownBy(() -> recruitmentBoard.modify(noneWriterId, newStartingDate, newActivityCategory, newTitleValue, newContentValue))
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
        writer.updateProfileImage(testUrl, "test_Image");
        RecruitmentBoard recruitmentBoard = new RecruitmentBoard(now, writer, LocalDate.of(2025, 2, 11), "달리기",
                "제목", "본문내용");

        //when
        String writerProfileImageUrl = recruitmentBoard.getWriterProfileImageUrl();

        //then
        assertThat(writerProfileImageUrl).isEqualTo(testUrl);
    }
}
