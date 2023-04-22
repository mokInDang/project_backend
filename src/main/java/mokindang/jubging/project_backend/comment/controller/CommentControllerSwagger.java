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
import mokindang.jubging.project_backend.comment.service.response.BoardIdResponse;
import mokindang.jubging.project_backend.exception.ErrorResponse;
import mokindang.jubging.project_backend.web.argumentresolver.Login;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

@Tag(name = "댓글 서비스", description = "댓글 관련 api")
public interface CommentControllerSwagger {

    @Operation(summary = "구인 게시판에 새 댓글 작성", parameters = {
            @Parameter(name = AUTHORIZATION, description = "access token", in = ParameterIn.HEADER, required = true),
            @Parameter(name = SET_COOKIE, description = "refreshToken", in = ParameterIn.COOKIE, required = true)}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "새 댓글 작성"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 유저 \t\n" +
                    "존재하지 않는 게시글 \t\n" +
                    "유효하지 않은 댓글 길이 \t\n" +
                    "존재하지 않는 게시판에 대한 접근 \t\n",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/{board-type}/{boardId}/comments")
    ResponseEntity<BoardIdResponse> addCommentToBoard(@Login final Long memberId, @PathVariable("board-type") final BoardType boardType,
                                                      @PathVariable final Long boardId,
                                                      @Valid @RequestBody final CommentCreationRequest commentCreationRequest);
}
