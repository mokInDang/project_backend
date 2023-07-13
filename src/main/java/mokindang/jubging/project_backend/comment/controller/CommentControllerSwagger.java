package mokindang.jubging.project_backend.comment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mokindang.jubging.project_backend.comment.service.BoardType;
import mokindang.jubging.project_backend.comment.service.request.CommentCreationRequest;
import mokindang.jubging.project_backend.comment.service.request.ReplyCommentCreationRequest;
import mokindang.jubging.project_backend.comment.service.response.BoardIdResponse;
import mokindang.jubging.project_backend.comment.service.response.CommentIdResponse;
import mokindang.jubging.project_backend.comment.service.response.MultiCommentSelectionResponse;
import mokindang.jubging.project_backend.exception.ErrorResponse;
import mokindang.jubging.project_backend.web.argumentresolver.Login;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Tag(name = "댓글 서비스", description = "댓글 관련 api")
public interface CommentControllerSwagger {

    @Operation(summary = "게시글에 새 댓글 작성", parameters = {
            @Parameter(name = AUTHORIZATION, description = "access token", in = ParameterIn.HEADER, required = true)
    }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "새 댓글 작성"),
            @ApiResponse(responseCode = "400", description = "존재하지 않은 유저 \t\n" +
                    "존재하지 않는 게시글 \t\n" +
                    "유효하지 않은 댓글 길이 \t\n" +
                    "존재하지 않는 게시판에 대한 접근 \t\n",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/{board-type}/{boardId}/comments")
    ResponseEntity<BoardIdResponse> addCommentToBoard(@Parameter(hidden = true) @Login final Long memberId,
                                                      @PathVariable("board-type") final BoardType boardType,
                                                      @PathVariable final Long boardId,
                                                      @Valid @RequestBody final CommentCreationRequest commentCreationRequest);

    @Operation(summary = "게시글의 댓글 목록 조회", parameters = {
            @Parameter(name = AUTHORIZATION, description = "access token", in = ParameterIn.HEADER, required = true)
    }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 리스트 조회 완료"),
            @ApiResponse(responseCode = "400", description =
                    "존재하지 않는 댓글에 대한 접근 \t\n" +
                            "유효하지 않은 게시글 종류에 대한 접근 \t\n",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping("/{board-type}/{boardId}/comments")
    ResponseEntity<MultiCommentSelectionResponse> selectComments(@Parameter(hidden = true) @Login final Long memberId,
                                                                 @PathVariable("board-type") final BoardType boardType,
                                                                 @PathVariable final Long boardId);

    @Operation(summary = "댓글 삭제", parameters = {
            @Parameter(name = AUTHORIZATION, description = "access token", in = ParameterIn.HEADER, required = true)
    }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "댓글 삭제 완료"),
            @ApiResponse(responseCode = "400", description =
                    "존재하지 않는 댓글에 대한 접근 \t\n",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @DeleteMapping("/comments/{commentId}")
    ResponseEntity<Object> removeComment(@Parameter(hidden = true) @Login Long memberId, @PathVariable final Long commentId);

    @Operation(summary = "대댓글 생성", parameters = {
            @Parameter(name = AUTHORIZATION, description = "access token", in = ParameterIn.HEADER, required = true)}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "대댓글 생성 완료"),
            @ApiResponse(responseCode = "400", description =
                    "존재하지 않는 댓글에 대한 접근 \t\n",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/comments/{commentId}/reply-comment")
    ResponseEntity<CommentIdResponse> addReplyComment(@Parameter(hidden = true) @Login final Long memberId,
                                                      @PathVariable Long commentId,
                                                      @RequestBody @Valid final
                                                      ReplyCommentCreationRequest replyCommentCreationRequest);

    @Operation(summary = "대댓글 삭제", parameters = {
            @Parameter(name = AUTHORIZATION, description = "access token", in = ParameterIn.HEADER, required = true)
    }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "대댓글 삭제 완료"),
            @ApiResponse(responseCode = "400", description =
                    "존재하지 않는 댓글에 대한 접근 \t\n",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @DeleteMapping("/comments/reply-comments/{replyCommentId}")
    ResponseEntity<Object> deleteReplyComment(@Login final Long memberId, @PathVariable Long replyCommentId);
}
