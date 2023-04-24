package mokindang.jubging.project_backend.comment.domain;

import mokindang.jubging.project_backend.member.domain.Member;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.Mockito.mock;

class ReplyCommentTest {

    @Test
    @DisplayName("부모 댓글, 작성 일시, 작성자, 댓글 본문을 입력받아 댓글을 생성한다.")
    void create() {
        //given
        Comment comment = mock(Comment.class);

        LocalDateTime now = LocalDateTime.of(2023, 4, 8, 16, 48);
        Member writer = new Member("test@email.com", "test");
        String commentBodyValue = "안녕하세요.";

        //when, then
        assertThatCode(() -> new ReplyComment(comment, commentBodyValue, writer, now)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("부모 댓글, 작성 일시, 작성자, 댓글 본문을 반환한다.")
    void getter() {
        //given
        SoftAssertions softly = new SoftAssertions();
        ReplyComment replyComment = createReplyCommentForTest();

        //when, then
        softly.assertThat(replyComment.getReplyCommentBody().getBody()).isEqualTo("안녕하세요.");
        softly.assertThat(replyComment.getCreatedDateTime()).isEqualTo(LocalDateTime.of(2023, 4, 8, 16, 48));
        softly.assertThat(replyComment.getWriterAlias()).isEqualTo("test");
        softly.assertThat(replyComment.getFirstFourDigitsOfWriterEmail()).isEqualTo("test");
        softly.assertThat(replyComment.getWriterProfileImageUrl()).isEqualTo("test_url");
        softly.assertThat(replyComment.wasEdited()).isFalse();
        softly.assertAll();
    }

    private ReplyComment createReplyCommentForTest() {
        Comment comment = mock(Comment.class);
        LocalDateTime now = LocalDateTime.of(2023, 4, 8, 16, 48);
        Member writer = new Member("test@email.com", "test");
        writer.updateProfileImage("test_url", "test_image");
        String replyCommentBodyValue = "안녕하세요.";
        return new ReplyComment(comment, replyCommentBodyValue, writer, now);
    }

    @Test
    @DisplayName("게시글 내용 변경한다. 이때 마지막 수정일을 변경한다.")
    void modify() {
        //given
        Comment comment = mock(Comment.class);

        SoftAssertions softly = new SoftAssertions();
        ReplyComment replyComment = createReplyCommentForTest();
        String newCommentBody = "하이~~";
        LocalDateTime now = LocalDateTime.of(2023, 5, 9, 11, 11, 11);

        //when
        replyComment.modify(newCommentBody, now);

        //then
        softly.assertThat(replyComment.getReplyCommentBody().getBody()).isEqualTo(newCommentBody);
        softly.assertThat(replyComment.getLastModifiedDateTime()).isEqualTo(now);
        softly.assertAll();
    }

    @Test
    @DisplayName("게시글 수정 여부를 반환한다.")
    void wasEdited() {
        //given
        ReplyComment replyComment = createReplyCommentForTest();
        String newCommentBody = "하이~~";
        LocalDateTime now = LocalDateTime.of(2023, 5, 9, 11, 11, 11);
        replyComment.modify(newCommentBody, now);

        //when
        boolean actual = replyComment.wasEdited();

        //then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("대댓글 작성자의 프로필 이미지 url 을 반환한다.")
    void getWriterProfileImageUrl() {
        //given
        ReplyComment replyComment = createReplyCommentForTest();

        //when
        String actual = replyComment.getWriterProfileImageUrl();

        //then
        assertThat(actual).isEqualTo("test_url");
    }

    @Test
    @DisplayName("대댓글 작성자의 닉네임을 반환한다.")
    void getWriterAlias() {
        //given
        ReplyComment replyComment = createReplyCommentForTest();

        //when
        String actual = replyComment.getWriterProfileImageUrl();

        //then
        assertThat(actual).isEqualTo("test");
    }

    @Test
    @DisplayName("게시글 작성자의 이메일 앞 4자리를 반환한다.")
    void getFirstFourDigitsOfWriterEmail() {
        //given
        ReplyComment replyComment = createReplyCommentForTest();

        //when
        String actual = replyComment.getFirstFourDigitsOfWriterEmail();

        //then
        assertThat(actual).isEqualTo("test");
    }
}
