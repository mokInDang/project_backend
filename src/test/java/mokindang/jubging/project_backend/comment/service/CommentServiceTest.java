package mokindang.jubging.project_backend.comment.service;

import mokindang.jubging.project_backend.comment.repository.CommentRepository;
import mokindang.jubging.project_backend.comment.service.request.CommentCreationRequest;
import mokindang.jubging.project_backend.member.domain.Member;
import mokindang.jubging.project_backend.member.service.MemberService;
import mokindang.jubging.project_backend.recruitment_board.domain.RecruitmentBoard;
import mokindang.jubging.project_backend.recruitment_board.service.RecruitmentBoardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        commentService.addComment(1L, BoardType.RECRUITMENT_BOARD,1L, commentCreateRequest);

        //then
        verify(commentRepository, times(1)).save(any());
    }

    private CommentCreationRequest createCommentCreateRequest() {
        return new CommentCreationRequest("댓글 예시 입니다.");
    }

}
