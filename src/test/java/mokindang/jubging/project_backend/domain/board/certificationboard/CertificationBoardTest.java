package mokindang.jubging.project_backend.domain.board.certificationboard;

import mokindang.jubging.project_backend.certification_board.domain.CertificationBoard;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.ContentBody;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.Title;
import mokindang.jubging.project_backend.member.domain.Member;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class CertificationBoardTest {

    @Test
    @DisplayName("인증 게시판 생성 시 생성 시간, 마지막 수정일, 게시글 작성자, 제목, 본문을 반환한다.")
    void getter() {
        //given
        SoftAssertions softly = new SoftAssertions();
        LocalDateTime now = LocalDateTime.of(2023, 11, 12, 0, 0, 0);
        Member writer = new Member("test1@email.com", "test");
        writer.updateRegion("동작구");
        CertificationBoard certificationBoard = new CertificationBoard(now, now, writer, "게시판 제목", "게시판 내용");

        //when
        LocalDateTime createdDateTime = certificationBoard.getCreatedDateTime();
        LocalDateTime modifiedTime = certificationBoard.getModifiedTIme();
        Member findWriter = certificationBoard.getWriter();
        Title title = certificationBoard.getTitle();
        ContentBody contentBody = certificationBoard.getContentBody();

        //then
        softly.assertThat(createdDateTime).isEqualTo(now);
        softly.assertThat(modifiedTime).isEqualTo(now);
        softly.assertThat(findWriter).isEqualTo(writer);
        softly.assertThat(title).isEqualTo(new Title("게시판 제목"));
        softly.assertThat(contentBody).isEqualTo(new ContentBody("게시판 내용"));
        softly.assertAll();
    }
}
