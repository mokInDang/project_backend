package mokindang.jubging.project_backend.comment.service.strategy;

import mokindang.jubging.project_backend.comment.domain.Comment;
import mokindang.jubging.project_backend.comment.repository.CommentRepository;
import mokindang.jubging.project_backend.comment.service.BoardType;
import mokindang.jubging.project_backend.comment.service.response.MultiCommentSelectionResponse;
import mokindang.jubging.project_backend.comment.service.response.commentresponse.RecruitmentBoardCommentResponse;
import mokindang.jubging.project_backend.member.domain.Member;
import mokindang.jubging.project_backend.member.domain.vo.ProfileImage;
import mokindang.jubging.project_backend.member.domain.vo.Region;
import mokindang.jubging.project_backend.member.service.MemberService;
import mokindang.jubging.project_backend.recruitment_board.domain.RecruitmentBoard;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.place.Coordinate;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.place.Place;
import mokindang.jubging.project_backend.recruitment_board.service.RecruitmentBoardService;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecruitmentBoardCommentsSelectionStrategyTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private MemberService memberService;

    @Mock
    private RecruitmentBoardService recruitmentBoardService;

    @InjectMocks
    private RecruitmentBoardCommentsSelectionStrategy recruitmentBoardCommentsSelectionStrategy;

    @Test
    @DisplayName("구인 게시글에 달린 대댓글을 포함한 댓글 리스트를 반환한다. " +
            "이때 댓글과 대댓글에 해당 댓글 작성자의 게시글 참여 상태를 포함하여 반환한다.")
    void selectComments() {
        //given
        SoftAssertions softly = new SoftAssertions();
        Member member = mock(Member.class);
        when(member.getId()).thenReturn(1L);
        when(member.getRegion()).thenReturn(Region.from("동작구"));
        long otherMemberId = 2L;

        List<Comment> testComments = List.of(createTestComment(1L, member.getId()), createTestComment(1L, otherMemberId));
        when(commentRepository.findCommentByBoardTypeAndBoardId(BoardType.RECRUITMENT_BOARD, 1L)).thenReturn(testComments);

        when(memberService.findByMemberId(anyLong())).thenReturn(member);
        RecruitmentBoard recruitmentBoard = createRecruitmentBoard(member);
        when(recruitmentBoardService.findByIdWithOptimisticLock(anyLong())).thenReturn(recruitmentBoard);

        int indexOfParticipatedMemberComment = 0;
        int indexOfNoneParticipatedMemberComment = 1;

        //when
        MultiCommentSelectionResponse multiCommentSelectionResponse = recruitmentBoardCommentsSelectionStrategy.selectComments(1L, member.getId());

        //then
        softly.assertThat(multiCommentSelectionResponse.getComments().size()).isEqualTo(2);
        softly.assertThat(multiCommentSelectionResponse.getCountOfCommentAndReplyComment()).isEqualTo(2);
        RecruitmentBoardCommentResponse participatedMemberComment = (RecruitmentBoardCommentResponse) multiCommentSelectionResponse.getComments().get(indexOfParticipatedMemberComment);
        RecruitmentBoardCommentResponse noneParticipatedMemberComment = (RecruitmentBoardCommentResponse) multiCommentSelectionResponse.getComments().get(indexOfNoneParticipatedMemberComment);
        softly.assertThat(participatedMemberComment.isWriterParticipatedIn()).isTrue();
        softly.assertThat(noneParticipatedMemberComment.isWriterParticipatedIn()).isFalse();
        softly.assertAll();
    }

    private Comment createTestComment(final Long boardId, final Long writerId) {
        LocalDateTime createdDateTime = LocalDateTime.of(2023, 11, 11, 3, 12);
        Member writer = mock(Member.class);
        when(writer.getId()).thenReturn(writerId);
        when(writer.getProfileImage()).thenReturn(new ProfileImage("test_url"));
        when(writer.getFirstFourDigitsOfWriterEmail()).thenReturn("test");
        when(writer.getAlias()).thenReturn("testuser");
        return new Comment(boardId, BoardType.CERTIFICATION_BOARD, "본문", writer, createdDateTime);
    }

    private RecruitmentBoard createRecruitmentBoard(final Member writer) {
        LocalDateTime createdDateTime = LocalDateTime.of(2023, 11, 11, 3, 12);
        LocalDate startingDate = LocalDate.of(2023, 12, 25);
        RecruitmentBoard recruitmentBoard = new RecruitmentBoard(createdDateTime, writer, startingDate,
                "달리기", createTestPlace(), "제목", "본문", 8);
        return recruitmentBoard;
    }

    private Place createTestPlace() {
        Coordinate coordinate = new Coordinate(1.1, 1.2);
        return new Place(coordinate, "서울시 동작구 상도동 1-1");
    }

    @ParameterizedTest
    @CsvSource(value = {"동작구, true", "성동구, false"})
    @DisplayName("구인 게시글에 달린 대댓글을 포함한 댓글 리스트를 반환한다. " +
            "이때 조회를 한 회원과 게시글의 지역이 같으면 댓글 작성 권한(writingCommentPermission) 을 true로 반환한다.")
    void selectCommentsAndCheckWritingCommentPermission(final String memberRegion, final boolean expected) {
        //given
        List<Comment> testComments = List.of(createTestComment(1L, 1L), createTestComment(1L, 2L));
        when(commentRepository.findCommentByBoardTypeAndBoardId(BoardType.RECRUITMENT_BOARD, 1L))
                .thenReturn(testComments);

        Member boardWriter = mock(Member.class);
        when(boardWriter.getId()).thenReturn(1L);
        when(boardWriter.getRegion()).thenReturn(Region.from("동작구"));
        RecruitmentBoard recruitmentBoard = createRecruitmentBoard(boardWriter);
        when(recruitmentBoardService.findByIdWithOptimisticLock(anyLong())).thenReturn(recruitmentBoard);

        Member member = mock(Member.class);
        when(member.getRegion()).thenReturn(Region.from(memberRegion));
        when(memberService.findByMemberId(anyLong())).thenReturn(member);

        //when
        MultiCommentSelectionResponse multiCommentSelectionResponse = recruitmentBoardCommentsSelectionStrategy.selectComments(1L, 2L);

        //then
        assertThat(multiCommentSelectionResponse.isWritingCommentPermission()).isEqualTo(expected);
    }
}
