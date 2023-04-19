package mokindang.jubging.project_backend.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mokindang.jubging.project_backend.comment.service.CommentService;
import mokindang.jubging.project_backend.comment.service.request.CommentCreationRequest;
import mokindang.jubging.project_backend.recruitment_board.service.response.RecruitmentBoardIdResponse;
import mokindang.jubging.project_backend.web.argumentresolver.Login;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/recruitment-board/{boardId}/comments")
    public ResponseEntity<RecruitmentBoardIdResponse> addCommentToRecruitmentBoard(@Login final Long memberId, @PathVariable final Long boardId,
                                                                                   @Valid @RequestBody final CommentCreationRequest commentCreationRequest) {
        log.info("memberId ={} 의 RecruitmentBoardId = {} 에 대한 댓글 작성 요청", memberId, boardId);
        RecruitmentBoardIdResponse recruitmentBoardIdResponse = commentService.addCommentToRecruitmentBoard(memberId, boardId, commentCreationRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recruitmentBoardIdResponse);
    }
}
