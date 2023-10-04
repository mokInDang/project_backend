package mokindang.jubging.project_backend.comment.service.strategy;

import mokindang.jubging.project_backend.certification_board.domain.CertificationBoard;
import mokindang.jubging.project_backend.certification_board.service.CertificationBoardService;
import mokindang.jubging.project_backend.comment.domain.Comment;
import mokindang.jubging.project_backend.comment.repository.CommentRepository;
import mokindang.jubging.project_backend.comment.service.BoardType;
import mokindang.jubging.project_backend.comment.service.response.MultiCommentSelectionResponse;
import mokindang.jubging.project_backend.member.domain.Member;
import mokindang.jubging.project_backend.member.domain.vo.ProfileImage;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CertificationBoardCommentsSelectionStrategyTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CertificationBoardService certificationBoardService;

    @InjectMocks
    private CertificationBoardCommentsSelectionStrategy commentsSelectionStrategy;

    @Test
    @DisplayName("인증 게시글에 있는 댓글들을 반환한다. 이때 해당 댓글에 대한 작성 권한은 항상 true 이다.")
    void selectComments() {
        //given
        SoftAssertions softly = new SoftAssertions();
        List<Comment> testComment = List.of(createTestComment(1L), createTestComment(1L));
        when(commentRepository.findCommentByBoardTypeAndBoardId(BoardType.CERTIFICATION_BOARD, 1L))
                .thenReturn(testComment);
        CertificationBoard certificationBoard = createCertificationBoard();
        when(certificationBoardService.findById(1L)).thenReturn(certificationBoard);

        //when
        MultiCommentSelectionResponse multiCommentSelectionResponse = commentsSelectionStrategy.selectComments(1L, 2L);

        //then
        softly.assertThat(multiCommentSelectionResponse.getComments().size()).isEqualTo(2);
        softly.assertThat(multiCommentSelectionResponse.getCountOfCommentAndReplyComment()).isEqualTo(2);
        softly.assertThat(multiCommentSelectionResponse.isWritingCommentPermission()).isTrue();
        softly.assertAll();
    }

    private Comment createTestComment(Long boardId) {
        LocalDateTime createdDateTime = LocalDateTime.of(2023, 11, 11, 3, 12);
        Member writer = mock(Member.class);
        when(writer.getId()).thenReturn(1L);
        when(writer.getProfileImage()).thenReturn(new ProfileImage("test_url"));
        when(writer.getFirstFourDigitsOfWriterEmail()).thenReturn("test");
        when(writer.getAlias()).thenReturn("testuser");

        return new Comment(boardId, BoardType.CERTIFICATION_BOARD, "본문", writer, createdDateTime);
    }

    private static CertificationBoard createCertificationBoard() {
        LocalDateTime now = LocalDateTime.of(2023, 11, 11, 3, 12);
        Member writer = mock(Member.class);
        when(writer.getId()).thenReturn(1L);
        CertificationBoard certificationBoard = new CertificationBoard(now, now, writer, "제목", "본문");
        return certificationBoard;
    }
}
