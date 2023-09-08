package mokindang.jubging.project_backend.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mokindang.jubging.project_backend.comment.domain.Comment;
import mokindang.jubging.project_backend.comment.domain.ReplyComment;
import mokindang.jubging.project_backend.comment.repository.CommentRepository;
import mokindang.jubging.project_backend.comment.repository.ReplyCommentRepository;
import mokindang.jubging.project_backend.comment.service.request.CommentCreationRequest;
import mokindang.jubging.project_backend.comment.service.request.ReplyCommentCreationRequest;
import mokindang.jubging.project_backend.comment.service.response.BoardIdResponse;
import mokindang.jubging.project_backend.comment.service.response.CommentIdResponse;
import mokindang.jubging.project_backend.comment.service.response.MultiCommentSelectionResponse;
import mokindang.jubging.project_backend.comment.service.strategy.CommentsSelectionStrategy;
import mokindang.jubging.project_backend.comment.service.strategy.CommentsSelectionStrategyFinder;
import mokindang.jubging.project_backend.member.domain.Member;
import mokindang.jubging.project_backend.member.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final ReplyCommentRepository replyCommentRepository;
    private final CommentRepository commentRepository;
    private final MemberService memberService;
    private final CommentsSelectionStrategyFinder commentsSelectionStrategyFinder;

    @Transactional
    public BoardIdResponse addComment(final Long memberId, final BoardType boardType, final Long boardId,
                                      final CommentCreationRequest commentCreationRequest) {
        Member writer = memberService.findByMemberId(memberId);
        LocalDateTime now = LocalDateTime.now();
        Comment comment = new Comment(boardId, boardType, commentCreationRequest.getCommentBody(), writer, now);
        commentRepository.save(comment);
        return new BoardIdResponse(comment.getBoardId());
    }

    @Transactional
    public MultiCommentSelectionResponse selectComments(final Long memberId, final BoardType boardType, final Long boardId) {
        CommentsSelectionStrategy commentSelectionStrategy = commentsSelectionStrategyFinder.getCommentSelectionStrategy(boardType);
        return commentSelectionStrategy.selectComments(boardId, memberId);
    }

    @Transactional
    public void deleteComment(final Long memberId, final Long commentId) {
        Comment comment = findById(commentId);
        comment.validatePermission(memberId);
        commentRepository.delete(comment);
    }

    private Comment findById(final Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글 입니다."));
    }

    @Transactional
    public CommentIdResponse addReplyComment(final Long memberId, final Long commentId,
                                             final ReplyCommentCreationRequest replyCommentCreationRequest) {
        Member writer = memberService.findByMemberId(memberId);
        Comment comment = findById(commentId);
        LocalDateTime now = LocalDateTime.now();
        ReplyComment replyComment = new ReplyComment(comment, replyCommentCreationRequest.getReplyCommentBody(), writer, now);
        ReplyComment savedReplyComment = replyCommentRepository.save(replyComment);
        return new CommentIdResponse(savedReplyComment.getComment().getId());
    }

    @Transactional
    public void deleteReplyComment(final Long memberId, final Long replyCommentId) {
        ReplyComment replyComment = replyCommentRepository.findById(replyCommentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 대댓글 입니다."));
        replyComment.validatePermission(memberId);
        replyCommentRepository.delete(replyComment);
    }
}
