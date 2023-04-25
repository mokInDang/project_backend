package mokindang.jubging.project_backend.comment.service;

import mokindang.jubging.project_backend.comment.domain.Comment;
import mokindang.jubging.project_backend.comment.domain.ReplyComment;
import mokindang.jubging.project_backend.comment.domain.vo.CommentBody;
import mokindang.jubging.project_backend.comment.repository.CommentRepository;
import mokindang.jubging.project_backend.comment.repository.ReplyCommentRepository;
import mokindang.jubging.project_backend.comment.service.request.CommentCreationRequest;
import mokindang.jubging.project_backend.comment.service.request.ReplyCommentCreationRequest;
import mokindang.jubging.project_backend.comment.service.response.CommentIdResponse;
import mokindang.jubging.project_backend.comment.service.response.MultiCommentSelectionResponse;
import mokindang.jubging.project_backend.member.domain.Member;
import mokindang.jubging.project_backend.member.service.MemberService;
import mokindang.jubging.project_backend.recruitment_board.domain.RecruitmentBoard;
import mokindang.jubging.project_backend.recruitment_board.service.RecruitmentBoardService;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private MemberService memberService;

    @Mock
    private RecruitmentBoardService recruitmentBoardService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ReplyCommentRepository replyCommentRepository;

    @InjectMocks
    private CommentService commentService;


    @Test
    @DisplayName("작성 회원의 id, 게시글 번호, CommentCreationRequest 를 입력받아 댓글을 작성한다.")
    void addCommentToRecruitmentBoard() {
        //given
        Member member = mock(Member.class);
        when(memberService.findByMemberId(anyLong())).thenReturn(member);
        RecruitmentBoard recruitmentBoard = mock(RecruitmentBoard.class);
        when(recruitmentBoardService.findById(anyLong())).thenReturn(recruitmentBoard);

        CommentCreationRequest commentCreateRequest = createCommentCreateRequest();

        //when
        commentService.addComment(1L, BoardType.RECRUITMENT_BOARD, 1L, commentCreateRequest);

        //then
        verify(commentRepository, times(1)).save(any());
    }

    private CommentCreationRequest createCommentCreateRequest() {
        return new CommentCreationRequest("댓글 예시 입니다.");
    }

    @Test
    @DisplayName("입력 받은 게시물의 댓글 리스트를 반환한다.")
    void selectComments() {
        //given
        SoftAssertions softly = new SoftAssertions();
        Comment comment1 = createMockedComment(1L);
        Comment comment2 = createMockedComment(2L);


        when(commentRepository.findCommentsByRecruitmentBoardId(anyLong())).thenReturn(List.of(comment1, comment2));
        ReplyComment replyComment = createMockedReplyComment();
        when(replyCommentRepository.findReplyCommentsByCommentId(anyLong())).thenReturn(List.of(replyComment));

        //when
        MultiCommentSelectionResponse multiCommentSelectionResponse = commentService.selectComments(1L, BoardType.RECRUITMENT_BOARD, 1L);


        //then
        softly.assertThat(multiCommentSelectionResponse.getComments()).hasSize(2);
        softly.assertThat(multiCommentSelectionResponse.getComments().get(0).getMultiReplyCommentSelectionResponse().getReplyComments()).hasSize(1);
        softly.assertThat(multiCommentSelectionResponse.getComments().get(0).getMultiReplyCommentSelectionResponse().getCountOfReplyComments()).isEqualTo(1);
        softly.assertAll();
    }

    private ReplyComment createMockedReplyComment() {
        ReplyComment replyComment = mock(ReplyComment.class);
        when(replyComment.getId()).thenReturn(1L);
        when(replyComment.getReplyCommentBody()).thenReturn(new CommentBody("본문내용"));
        when(replyComment.isSameWriterId(any())).thenReturn(true);
        when(replyComment.getCreatedDateTime()).thenReturn(LocalDateTime.of(2023, 11, 11, 11, 11, 1));
        when(replyComment.getWriterAlias()).thenReturn("대댓글작성자");
        when(replyComment.getFirstFourDigitsOfWriterEmail()).thenReturn("test");
        when(replyComment.getWriterProfileImageUrl()).thenReturn("test_url");
        return replyComment;
    }

    private Comment createMockedComment(final Long commentId) {
        Comment comment = mock(Comment.class);
        when(comment.getId()).thenReturn(commentId);
        when(comment.getCommentBody()).thenReturn(new CommentBody("본문내용"));
        when(comment.isSameWriterId(any())).thenReturn(true);
        when(comment.getCreatedDateTime()).thenReturn(LocalDateTime.of(2023, 11, 11, 11, 11, 1));
        when(comment.getWriterAlias()).thenReturn("댓글작성자");
        when(comment.getFirstFourDigitsOfWriterEmail()).thenReturn("test");
        when(comment.getWriterProfileImageUrl()).thenReturn("test_url");
        return comment;
    }

    @Test
    @DisplayName("입력 받은 commentId 에 해당하는 댓글을 삭제한다")
    void deleteComment() {
        //given
        Comment comment = mock(Comment.class);
        when(commentRepository.findById(anyLong())).thenReturn(Optional.ofNullable(comment));

        //when
        commentService.deleteComment(1L, 1L);

        //then
        verify(commentRepository, times(1)).delete(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 삭제시 입력받은 commentId 에 해당하는 Comment 가 없으면 예외를 반환한다.")
    void deleteFailedByNoneExistComment() {
        //given
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> commentService.deleteComment(1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 댓글 입니다.");
    }

    @Test
    @DisplayName("입력받은 대댓글을 추가한다.")
    void addReplyComment() {
        //given
        Member writer = mock(Member.class);
        when(memberService.findByMemberId(anyLong())).thenReturn(writer);
        Comment comment = mock(Comment.class);
        when(commentRepository.findById(anyLong())).thenReturn(Optional.ofNullable(comment));
        ReplyComment replyComment = mock(ReplyComment.class);
        when(replyCommentRepository.save(any(ReplyComment.class))).thenReturn(replyComment);
        when(replyComment.getComment()).thenReturn(comment);
        when(replyComment.getComment().getId()).thenReturn(1L);

        ReplyCommentCreationRequest replyCommentCreationRequest = new ReplyCommentCreationRequest("대댓글 본문");
        //when
        CommentIdResponse commentIdResponse = commentService.addReplyComment(1L, 1L, replyCommentCreationRequest);

        //then
        assertThat(commentIdResponse.getCommentId()).isEqualTo(1L);
    }
    
    @Test
    @DisplayName("memberId 와 replyCommentId 를 받아 해당하는 대댓글 삭제를 한다.")
    void deleteReplyComment() {
        //given
        ReplyComment replyComment = mock(ReplyComment.class);
        when(replyCommentRepository.findById(anyLong())).thenReturn(Optional.ofNullable(replyComment));
        doNothing().when(replyComment)
                .validatePermission(anyLong());

        Long memberId = 1L;
        Long replyCommentId = 1L;

        //when
        commentService.deleteReplyComment(memberId, replyCommentId);

        //then
        verify(replyCommentRepository, times(1)).findById(anyLong());
    }
}
