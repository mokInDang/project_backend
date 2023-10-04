package mokindang.jubging.project_backend.certification_board;

import mokindang.jubging.project_backend.certification_board.domain.CertificationBoard;
import mokindang.jubging.project_backend.member.domain.Member;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.ContentBody;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.Title;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

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

    @Test
    @DisplayName("메인 이미지가 존재하지 않는 경우 main image url 조회시 예외를 반환한다.")
    void validateMainImageUrl() {
        //given
        LocalDateTime now = LocalDateTime.of(2023, 11, 12, 0, 0, 0);
        Member writer = new Member("test1@email.com", "test");
        CertificationBoard certificationBoard = new CertificationBoard(now, now, writer, "게시판 제목", "게시판 내용");

        //when, then
        assertThatThrownBy(() -> certificationBoard.getMainImageUrl()).isInstanceOf(IllegalStateException.class)
                .hasMessage("이미지가 존재하지 않습니다.");
    }
}
