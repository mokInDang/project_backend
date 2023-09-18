package mokindang.jubging.project_backend.comment.repository;

import mokindang.jubging.project_backend.comment.domain.Comment;
import mokindang.jubging.project_backend.comment.domain.ReplyComment;
import mokindang.jubging.project_backend.comment.repository.response.CommentCountResponse;
import mokindang.jubging.project_backend.comment.service.BoardType;
import mokindang.jubging.project_backend.member.domain.Member;
import mokindang.jubging.project_backend.member.repository.MemberRepository;
import mokindang.jubging.project_backend.recruitment_board.domain.RecruitmentBoard;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.place.Coordinate;
import mokindang.jubging.project_backend.recruitment_board.domain.vo.place.Place;
import mokindang.jubging.project_backend.recruitment_board.repository.RecruitmentBoardRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ReplyCommentRepository replyCommentRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RecruitmentBoardRepository recruitmentBoardRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Rollback
    @Test
    @DisplayName("boardId 리스트와 댓글 타입을 입력 받아, 각각의 댓글과 대댓글의 갯수를 반환한다.")
    void countCommentsByBoardId() {
        //given
        SoftAssertions softly = new SoftAssertions();
        LocalDateTime now = LocalDateTime.of(2023, 3, 27, 11, 11, 11);
        Member writer = createMember();
        memberRepository.save(writer);

        RecruitmentBoard boardA = createRecruitmentBoard(writer);
        RecruitmentBoard boardB = createRecruitmentBoard(writer);
        RecruitmentBoard savedBoardA = recruitmentBoardRepository.save(boardA);
        RecruitmentBoard savedBoardB = recruitmentBoardRepository.save(boardB);

        setCommentAndReplyComment(savedBoardA, writer, now, savedBoardB);
        entityManager.flush();

        //when
        List<CommentCountResponse> commentCountResponses = commentRepository.countALLCommentByBoardIds(List.of(savedBoardA.getId(), savedBoardB.getId()), BoardType.RECRUITMENT_BOARD);

        //then
        entityManager.clear();
        assertThat(commentCountResponses.get(0).getCommentCount()).isEqualTo(3);
        assertThat(commentCountResponses.get(1).getCommentCount()).isEqualTo(1);
    }

    private static Member createMember() {
        Member member = new Member("test@mail.com", "test");
        member.updateRegion("동작구");
        return member;
    }

    private RecruitmentBoard createRecruitmentBoard(final Member writer) {
        LocalDateTime now = LocalDateTime.of(2023, 3, 25, 1, 1);
        RecruitmentBoard recruitingRecruitmentBoardWithPastStartingDate = new RecruitmentBoard(now, writer, LocalDate.of(2023, 3, 27), "달리기", createTestPlace(), "제목", "본문", 8);
        return recruitingRecruitmentBoardWithPastStartingDate;
    }

    private Place createTestPlace() {
        Coordinate coordinate = new Coordinate(1.1, 1.2);
        return new Place(coordinate, "서울시 동작구 상도동 1-1");
    }

    private void setCommentAndReplyComment(RecruitmentBoard savedBoardA, Member writer, LocalDateTime now, RecruitmentBoard savedBoardB) {
        Comment commentAOnBoardA = new Comment(savedBoardA.getId(), BoardType.RECRUITMENT_BOARD, "a", writer, now);
        Comment commentBOnBoardA = new Comment(savedBoardA.getId(), BoardType.RECRUITMENT_BOARD, "b", writer, now);
        Comment commentCOnBoardB = new Comment(savedBoardB.getId(), BoardType.RECRUITMENT_BOARD, "c", writer, now);
        commentRepository.save(commentAOnBoardA);
        commentRepository.save(commentBOnBoardA);
        commentRepository.save(commentCOnBoardB);

        ReplyComment replyCommentAOnCommentAOnBoardA = new ReplyComment(commentAOnBoardA, "aa", writer, now);
        replyCommentRepository.save(replyCommentAOnCommentAOnBoardA);
    }
}
