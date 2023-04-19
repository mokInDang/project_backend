package mokindang.jubging.project_backend.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mokindang.jubging.project_backend.comment.domain.Comment;
import mokindang.jubging.project_backend.comment.repository.CommentRepository;
import mokindang.jubging.project_backend.comment.service.request.CommentCreationRequest;
import mokindang.jubging.project_backend.member.domain.Member;
import mokindang.jubging.project_backend.member.service.MemberService;
import mokindang.jubging.project_backend.recruitment_board.domain.RecruitmentBoard;
import mokindang.jubging.project_backend.recruitment_board.service.RecruitmentBoardService;
import mokindang.jubging.project_backend.recruitment_board.service.response.RecruitmentBoardIdResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final RecruitmentBoardService recruitmentBoardService;
    private final MemberService memberService;

    public RecruitmentBoardIdResponse addCommentToRecruitmentBoard(final Long memberId, final Long boardId,
                                                                   final CommentCreationRequest commentCreationRequest) {
        Member writer = memberService.findByMemberId(memberId);
        RecruitmentBoard board = recruitmentBoardService.findById(boardId);
        LocalDateTime now = LocalDateTime.now();
        Comment comment = new Comment(board, commentCreationRequest.getCommentBody(), writer, now);
        commentRepository.save(comment);
        return new RecruitmentBoardIdResponse(board.getId());
    }
}
