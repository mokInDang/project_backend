package mokindang.jubging.project_backend.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mokindang.jubging.project_backend.comment.service.BoardType;
import mokindang.jubging.project_backend.comment.service.CommentService;
import mokindang.jubging.project_backend.comment.service.request.CommentCreationRequest;
import mokindang.jubging.project_backend.comment.service.request.ReplyCommentCreationRequest;
import mokindang.jubging.project_backend.comment.service.response.BoardIdResponse;
import mokindang.jubging.project_backend.comment.service.response.CommentIdResponse;
import mokindang.jubging.project_backend.comment.service.response.MultiCommentSelectionResponse;
import mokindang.jubging.project_backend.web.argumentresolver.Login;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class CommentController implements CommentControllerSwagger {

    private final CommentService commentService;

    @PostMapping("/{board-type}/{boardId}/comments")
    public ResponseEntity<BoardIdResponse> addCommentToBoard(@Login final Long memberId,
                                                             @PathVariable("board-type") final BoardType boardType,
                                                             @PathVariable final Long boardId,
                                                             @Valid @RequestBody final CommentCreationRequest commentCreationRequest) {
        log.info("memberId ={} 의 게시판 {} BoardId = {} 에 대한 댓글 작성 요청", memberId, boardType.toString(), boardId);
        BoardIdResponse boardIdResponse = commentService.addComment(memberId, boardType, boardId, commentCreationRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(boardIdResponse);
    }

    @GetMapping("/{board-type}/{boardId}/comments")
    public ResponseEntity<MultiCommentSelectionResponse> selectComments(@Login final Long memberId,
                                                                        @PathVariable("board-type") final BoardType boardType,
                                                                        @PathVariable final Long boardId) {
        log.info("memberId = {} 의 게시판 {} boardId = {} 에 대한 댓글 조회 요청", memberId, boardType.toString(), boardId);
        MultiCommentSelectionResponse multiCommentSelectionResponse = commentService.selectComments(memberId, boardType, boardId);
        return ResponseEntity.ok()
                .body(multiCommentSelectionResponse);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Object> removeComment(@Login Long memberId, @PathVariable final Long commentId) {
        log.info("memberId = {} 의 commentId = {} 삭제 요청", memberId, commentId);
        commentService.deleteComment(memberId, commentId);
        return ResponseEntity.noContent()
                .build();
    }

    @PostMapping("/comments/{commentId}/reply-comment")
    public ResponseEntity<CommentIdResponse> addReplyComment(@Login final Long memberId,
                                                             @PathVariable Long commentId,
                                                             @RequestBody @Valid final
                                                             ReplyCommentCreationRequest replyCommentCreationRequest) {
        log.info("memberId = {} 의 commentId = {} 에 추가 대댓글 작성 요청");
        CommentIdResponse commentIdResponse = commentService.addReplyComment(memberId, commentId, replyCommentCreationRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentIdResponse);
    }

    @DeleteMapping("/comments/reply-comments/{replyCommentId}")
    public ResponseEntity<Object> deleteReplyComment(@Login final Long memberId,
                                                     @PathVariable Long replyCommentId) {
        log.info("memberId = {} 의 replyCommentId = {} 삭제 요청");
        commentService.deleteReplyComment(memberId, replyCommentId);
        return ResponseEntity.noContent()
                .build();
    }
}
