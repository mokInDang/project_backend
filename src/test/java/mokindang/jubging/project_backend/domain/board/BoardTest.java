//package mokindang.jubging.project_backend.domain.board;
//
//import mokindang.jubging.project_backend.domain.member.Member;
//import org.assertj.core.api.SoftAssertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.test.annotation.Rollback;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class BoardTest {
//
//    @Test
//    @DisplayName("게시판의 member(작성자), title, content 를 반환한다.")
//    public void getter() {
//        //given
//        SoftAssertions softly = new SoftAssertions();
//        Member testMember = new Member("koho1047@naver.com", "민호");
//        Board board = new Board(testMember,"게시판 제목", "게시판 내용 작성 테스트");
//
//        //when
//        Member member = board.getMember();
//        String title = board.getTitle();
//        String content = board.getContent();
//
//        //then
//        softly.assertThat(member).isEqualTo(testMember);
//        softly.assertThat(title).isEqualTo("게시판 제목");
//        softly.assertThat(content).isEqualTo("게시판 내용 작성 테스트");
//        softly.assertAll();
//    }
//
//}
