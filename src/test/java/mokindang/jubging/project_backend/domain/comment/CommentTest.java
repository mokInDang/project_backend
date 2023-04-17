package mokindang.jubging.project_backend.domain.comment;

import mokindang.jubging.project_backend.domain.member.Member;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

class CommentTest {

    @Test
    @DisplayName("작성 일시, 작성자, 댓글 본문을 입력받아 댓글을 생성한다.")
    void create() {
        //given
        LocalDateTime now = LocalDateTime.of(2023, 4, 8, 16, 48);
        Member writer = new Member("test@email.com", "test");
        String commentBodyValue = "안녕하세요.";

        //when, then
        assertThatCode(() -> new Comment(commentBodyValue, writer, now)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("작성 일시, 작성자, 댓글 본문을 반환한다.")
    void getter() {
        //given
        SoftAssertions softly = new SoftAssertions();
        LocalDateTime now = LocalDateTime.of(2023, 4, 8, 16, 48);
        Member writer = new Member("test@email.com", "test");
        String commentBodyValue = "안녕하세요.";

        //when
        Comment comment = new Comment(commentBodyValue, writer, now);

        //then
        softly.assertThat(comment.getCommentBody().getBody()).isEqualTo("안녕하세요.");
        softly.assertThat(comment.getWriter()).isEqualTo(writer);
        softly.assertThat(comment.getCreatedDateTime()).isEqualTo(now);
        softly.assertThat(comment.getLastModifiedDateTime()).isEqualTo(now);
        softly.assertAll();
    }

    @Test
    @DisplayName("게시글 내용 변경한다. 이때 마지막 수정일을 변경한다.")
    void modify() {
        //given
        SoftAssertions softly = new SoftAssertions();
        LocalDateTime createdTime = LocalDateTime.of(2023, 4, 8, 16, 48);
        Member writer = new Member("test@email.com", "test");
        String commentBodyValue = "안녕하세요.";
        Comment comment = new Comment(commentBodyValue, writer, createdTime);
        String newCommentBody = "하이~~";
        LocalDateTime now = LocalDateTime.of(2023, 5, 9, 11, 11, 11);

        //when
        comment.modify(newCommentBody, now);

        //then
        softly.assertThat(comment.getCommentBody().getBody()).isEqualTo(newCommentBody);
        softly.assertThat(comment.getLastModifiedDateTime()).isEqualTo(now);
        softly.assertAll();
    }
}
