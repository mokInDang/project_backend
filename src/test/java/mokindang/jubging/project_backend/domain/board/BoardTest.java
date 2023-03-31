package mokindang.jubging.project_backend.domain.board;

import mokindang.jubging.project_backend.domain.board.vo.Content;
import mokindang.jubging.project_backend.domain.board.vo.StartingDate;
import mokindang.jubging.project_backend.domain.board.vo.Title;
import mokindang.jubging.project_backend.domain.member.Member;
import mokindang.jubging.project_backend.domain.member.vo.Region;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback
@DataJpaTest
class BoardTest {

    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("게시글 생성 시, 모집 여부는 상태는 항상 true 이다.")
    void defaultOfOnRecruitment() {
        //given
        LocalDate now = LocalDate.of(2023, 11, 12);
        Member testMember = new Member("koho1047@naver.com", "민호");
        testMember.updateRegion("동작구");
        Board board = new Board(testMember, LocalDate.of(2025, 2, 11),
                "달리기", "게시판 제목", "게시판 내용 작성 테스트", now);

        //when
        boolean onRecruitment = board.isOnRecruitment();

        //then
        assertThat(onRecruitment).isTrue();
    }

    @Test
    @DisplayName("모집 여부를 마감한다.")
    void closeRecruitment() {
        //given
        LocalDate now = LocalDate.of(2023, 11, 12);
        Member testMember = new Member("koho1047@naver.com", "민호");
        testMember.updateRegion("동작구");
        Board board = new Board(testMember, LocalDate.of(2025, 2, 11),
                "달리기", "게시판 제목", "게시판 내용 작성 테스트", now);

        //when
        board.closeRecruitment();

        //then
        assertThat(board.isOnRecruitment()).isFalse();
    }

    @Test
    @DisplayName("게시글의 member(작성자), title, content, 활동 종류, 활동 시작일, 모집 여부, 작성 지역을 반환한다.")
    void getter() {
        //given
        SoftAssertions softly = new SoftAssertions();
        LocalDate now = LocalDate.of(2023, 11, 12);
        Member testMember = new Member("koho1047@naver.com", "민호");
        testMember.updateRegion("동작구");
        Board board = new Board(testMember, LocalDate.of(2025, 2, 11),
                "달리기", "게시판 제목", "게시판 내용 작성 테스트", now);

        //when
        Member member = board.getWriter();
        Title title = board.getTitle();
        Content content = board.getContent();
        ActivityCategory activityCategory = board.getActivityCategory();
        StartingDate startingDate = board.getStartingDate();
        Region region = board.getWritingRegion();
        boolean onRecruitment = board.isOnRecruitment();

        //then
        softly.assertThat(member).isEqualTo(testMember);
        softly.assertThat(activityCategory).isSameAs(ActivityCategory.RUNNING);
        softly.assertThat(startingDate).isEqualTo(new StartingDate(LocalDate.of(2023, 11, 11),
                LocalDate.of(2025, 2, 11)));
        softly.assertThat(title).isEqualTo(new Title("게시판 제목"));
        softly.assertThat(content).isEqualTo(new Content("게시판 내용 작성 테스트"));
        softly.assertThat(region).isEqualTo(member.getRegion());
        softly.assertThat(onRecruitment).isEqualTo(true);
        softly.assertAll();
    }

    @Test
    @DisplayName("계시글 생성 시, 유저의 지역이 인증되지 않은 상태면 예외를 반환한다.")
    void validateRegion() {
        //given
        Member member = new Member("Test@email.com", "test");
        LocalDate now = LocalDate.of(2023, 11, 12);

        //when, then
        assertThatThrownBy(() -> new Board(member, LocalDate.of(2025, 2, 11),
                "달리기", "게시판 제목", "게시판 내용 작성 테스트", now))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지역 인증이 되지 않아, 게시글을 생성할 수 없습니다.");
    }

    @Test
    @DisplayName("지역을 입력 받아, 게시글의 지역과 다르면 예외를 반환한다.")
    void checkRegion() {
        //given
        LocalDate now = LocalDate.of(2023, 11, 12);
        Member testMember = new Member("koho1047@naver.com", "민호");
        testMember.updateRegion("동작구");
        Board board = new Board(testMember, LocalDate.of(2025, 2, 11),
                "달리기", "게시판 제목", "게시판 내용 작성 테스트", now);

        //when, then
        assertThatThrownBy(() -> board.checkRegion(Region.from("성동구")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지역이 다른 유저는 게시글에 접근 할 수 없습니다.");
    }

    @Test
    @DisplayName("게시글 작성자의 별명을 반환한다.")
    void getWriterAlias() {
        //given
        LocalDate now = LocalDate.of(2023, 11, 12);
        Member testMember = new Member("koho1047@naver.com", "민호");
        testMember.updateRegion("동작구");
        Board board = new Board(testMember, LocalDate.of(2025, 2, 11),
                "달리기", "게시판 제목", "게시판 내용 작성 테스트", now);

        //when
        String actual = board.getWriter().getAlias();

        //then
        assertThat(actual).isEqualTo("민호");
    }

    @Test
    @DisplayName("입력받은 현재 날짜 보다 활동 시작일이 과거에 있으면 onRecruitment 를 false 로 변경한다.")
    void updateOnRecruitment() {
        //given
        LocalDate boardCreationDate = LocalDate.of(2023, 11, 12);
        Member testMember = new Member("koho1047@naver.com", "민호");
        testMember.updateRegion("동작구");
        LocalDate startingDate = LocalDate.of(2023, 11, 13);
        Board board = new Board(testMember, startingDate, "달리기", "게시판 제목", "게시판 내용 작성 테스트", boardCreationDate);

        LocalDate now = LocalDate.of(2023, 11, 14);
        //when
        board.updateOnRecruitmentByStaringDate(now);

        //then
        assertThat(board.isOnRecruitment()).isFalse();
    }

    @Test
    @DisplayName("게시글 작성자인지 확인한다. 작성자인경우 true 를 반환한다.")
    void isWriter() {
        //given
        LocalDate now = LocalDate.of(2023, 11, 12);
        Member writer = new Member("test1@email.com", "test");
        writer.updateRegion("동작구");
        Board board = new Board(writer, LocalDate.of(2025, 2, 11), "달리기",
                "제목", "본문내용", now);

        em.persist(writer);
        em.persist(board);
        em.flush();
        em.clear();

        //when
        boolean actual = board.isWriter(writer);

        //then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("게시글 작성자인지 확인한다. 작성자가 아닌경우 false 를 반환한다.")
    void isWriterWhenNoneWriter() {
        //given
        LocalDate now = LocalDate.of(2023, 11, 12);
        Member writer = new Member("test1@email.com", "test");
        writer.updateRegion("동작구");
        Board board = new Board(writer, LocalDate.of(2025, 2, 11), "달리기",
                "제목", "본문내용", now);

        Member noneWriter = new Member("noneWriter@test.com", "noneWriter");

        em.persist(writer);
        em.persist(board);
        em.persist(noneWriter);
        em.flush();
        em.clear();

        //when
        boolean actual = board.isWriter(noneWriter);

        //then
        assertThat(actual).isFalse();
    }
}
