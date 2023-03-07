package mokindang.jubging.project_backend.domain.board;

import mokindang.jubging.project_backend.domain.board.vo.Content;
import mokindang.jubging.project_backend.domain.board.vo.StartingDate;
import mokindang.jubging.project_backend.domain.board.vo.Title;
import mokindang.jubging.project_backend.domain.member.Member;
import mokindang.jubging.project_backend.domain.region.vo.Region;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class BoardTest {

    @Test
    @DisplayName("게시글 생성 시, 모집 여부는 상태는 항상 true 이다.")
    void defaultOfOnRecruitment() {
        //given
        LocalDate now = LocalDate.of(2023, 11, 12);
        Member testMember = new Member("koho1047@naver.com", "민호");
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
        Board board = new Board(testMember, LocalDate.of(2025, 2, 11),
                "달리기", "게시판 제목", "게시판 내용 작성 테스트", now);

        //when
        Member member = board.getMember();
        Title title = board.getTitle();
        Content content = board.getContent();
        ActivityCategory activityCategory = board.getActivityCategory();
        StartingDate startingDate = board.getStartingDate();
        Region region = board.getRegion();
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
}
