package mokindang.jubging.project_backend.controller.board;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mokindang.jubging.project_backend.exception.ErrorResponse;
import mokindang.jubging.project_backend.service.board.request.BoardCreateRequest;
import mokindang.jubging.project_backend.service.board.response.BoardSelectResponse;
import mokindang.jubging.project_backend.web.argumentresolver.Login;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

@Tag(name = "게시판", description = "게시판 관련 api")
public interface BoardControllerSwagger {

    @Operation(summary = "새글작성", parameters = {
            @Parameter(name = AUTHORIZATION, description = "access token", in = ParameterIn.HEADER, required = true),
            @Parameter(name = SET_COOKIE, description = "refreshToken", in = ParameterIn.COOKIE, required = true)}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "새글작성"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 유저 \t\n" +
                    "유효하지 않은 본문내용 \t\n" +
                    "유효하지 않은 제목 \t\n" +
                    "유효하지 않은 활동 시작일", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping
    ResponseEntity<Void> write(@Parameter(hidden = true) @Login Long memberId, @Valid @RequestBody final BoardCreateRequest boardCreateRequest);

    @Operation(summary = "게시글 조회", parameters = {
            @Parameter(name = "boardId", description = "Board 의 id", in = ParameterIn.PATH),
            @Parameter(name = AUTHORIZATION, description = "access token", in = ParameterIn.HEADER, required = true),
            @Parameter(name = SET_COOKIE, description = "refreshToken", in = ParameterIn.COOKIE, required = true)}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "글 조회"),
            @ApiResponse(responseCode = "400", description = "지역이 유효하지 않는 유저 \t\n" +
                    "존재하지 않는 게시글 조회", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),}
    )
    @GetMapping("/{boardId}")
    ResponseEntity<BoardSelectResponse> selectBoard(@Parameter(hidden = true) @Login Long memberId, @PathVariable final Long boardId);
}
