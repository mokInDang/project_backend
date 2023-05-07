package mokindang.jubging.project_backend.comment.domain;

import mokindang.jubging.project_backend.comment.domain.vo.CommentBody;
import mokindang.jubging.project_backend.member.domain.Member;
import mokindang.jubging.project_backend.recruitment_board.domain.RecruitmentBoard;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentTest {

    @Test
    @DisplayName("작성 일시, 작성자, 댓글 본문을 입력받아 댓글을 생성한다.")
    void create() {
        //given
        RecruitmentBoard recruitmentBoard = createRecruitmentBoard();
        LocalDateTime now = LocalDateTime.of(2023, 4, 8, 16, 48);
        Member writer = new Member("test@email.com", "test");
        String commentBodyValue = "안녕하세요.";

        //when, then
        assertThatCode(() -> Comment.createOnRecruitmentBoardWith(recruitmentBoard, commentBodyValue, writer, now)).doesNotThrowAnyException();
    }

    private RecruitmentBoard createRecruitmentBoard() {
        LocalDateTime now = LocalDateTime.of(2023, 11, 12, 0, 0, 0);
        Member writer = new Member("test1@email.com", "test");
        writer.updateRegion("동작구");
        return new RecruitmentBoard(now, writer, LocalDate.of(2025, 2, 11), "달리기",
                "제목", "본문내용");
    }

    @Test
    @DisplayName("작성 일시, 작성자, 댓글 본문, 수정 일시을 반환한다.")
    void getter() {
        //given
        SoftAssertions softly = new SoftAssertions();
        Comment comment = createTestComment();

        //when
        CommentBody commentBody = comment.getCommentBody();
        String writerAlias = comment.getWriterAlias();
        LocalDateTime createdDateTime = comment.getCreatedDateTime();
        LocalDateTime lastModifiedDateTime = comment.getLastModifiedDateTime();

        //then
        softly.assertThat(commentBody.getBody()).isEqualTo("안녕하세요.");
        softly.assertThat(writerAlias).isEqualTo("test");
        softly.assertThat(createdDateTime).isEqualTo(LocalDateTime.of(2023, 4, 8, 16, 48));
        softly.assertThat(lastModifiedDateTime).isEqualTo(LocalDateTime.of(2023, 4, 8, 16, 48));
        softly.assertAll();
    }

    private Comment createTestComment() {
        RecruitmentBoard recruitmentBoard = createRecruitmentBoard();
        LocalDateTime now = LocalDateTime.of(2023, 4, 8, 16, 48);
        Member writer = new Member("test@email.com", "test");
        writer.updateProfileImage("test_url");
        String commentBodyValue = "안녕하세요.";
        return Comment.createOnRecruitmentBoardWith(recruitmentBoard, commentBodyValue, writer, now);
    }

    @Test
    @DisplayName("게시글 내용 변경한다. 이때 마지막 수정일을 변경한다.")
    void modify() {
        //given
        Member writer = mock(Member.class);
        when(writer.getId()).thenReturn(1L);

        SoftAssertions softly = new SoftAssertions();
        RecruitmentBoard recruitmentBoard = createRecruitmentBoard();
        LocalDateTime createdTime = LocalDateTime.of(2023, 4, 8, 16, 48);

        String commentBodyValue = "안녕하세요.";
        Comment comment = Comment.createOnRecruitmentBoardWith(recruitmentBoard, commentBodyValue, writer, createdTime);
        String newCommentBody = "하이~~";
        LocalDateTime now = LocalDateTime.of(2023, 5, 9, 11, 11, 11);

        Long writerId = 1L;

        //when
        comment.modify(writerId, newCommentBody, now);

        //then
        softly.assertThat(comment.getCommentBody().getBody()).isEqualTo(newCommentBody);
        softly.assertThat(comment.getLastModifiedDateTime()).isEqualTo(now);
        softly.assertAll();
    }

    @Test
    @DisplayName("게시글이 수정 되었는지에 대한 여부를 반환한다.")
    void wasEdited() {
        //given
        Member writer = mock(Member.class);
        when(writer.getId()).thenReturn(1L);

        RecruitmentBoard recruitmentBoard = createRecruitmentBoard();
        LocalDateTime createdTime = LocalDateTime.of(2023, 4, 8, 16, 48);
        String commentBodyValue = "안녕하세요.";
        Comment comment = Comment.createOnRecruitmentBoardWith(recruitmentBoard, commentBodyValue, writer, createdTime);
        String newCommentBody = "하이~~";
        LocalDateTime now = LocalDateTime.of(2023, 5, 9, 11, 11, 11);
        comment.modify(1L, newCommentBody, now);

        //when
        boolean actual = comment.wasEdited();

        //then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("댓글 작성자의 닉네임을 반환한다.")
    void getWriterAlias() {
        //given
        Comment comment = createTestComment();

        //when
        String actual = comment.getWriterAlias();

        //then
        assertThat(actual).isEqualTo("test");
    }

    @Test
    @DisplayName("작성자의 이메일 앞 4자리를 반환한다.")
    void getFirstFourDigitsOfWriterEmail() {
        //given
        Comment comment = createTestComment();

        //when
        String actual = comment.getFirstFourDigitsOfWriterEmail();

        //then
        assertThat(actual).isEqualTo("test");
    }

    @Test
    @DisplayName("댓글 작성자의 프로필 url 을 반환한다.")
    void getWriterProfileImageUrl() {
        //given
        Comment comment = createTestComment();

        //when
        String actual = comment.getWriterProfileImageUrl();

        //then
        assertThat(actual).isEqualTo("test_url");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    @DisplayName("댓글에 달린 대댓글의 갯수를 반환한다.")
    void countReplyComment(final int countOfReplyComment) {
        //given
        Comment comment = createTestComment();
        addMockedReplyCommentByCountOfReplyComment(countOfReplyComment, comment);

        //when
        int actual = comment.countReplyComment();

        //then
        assertThat(actual).isEqualTo(countOfReplyComment);
    }

    private void addMockedReplyCommentByCountOfReplyComment(final int countOfReplyComment, final Comment comment) {
        ReplyComment replyComment = mock(ReplyComment.class);
        for (int i = 0; i < countOfReplyComment; i++) {
            comment.addReplyComment(replyComment);
        }
    }
}
