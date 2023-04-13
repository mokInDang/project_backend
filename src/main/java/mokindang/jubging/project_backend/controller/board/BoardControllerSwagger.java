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
import mokindang.jubging.project_backend.service.board.request.BoardCreationRequest;
import mokindang.jubging.project_backend.service.board.request.BoardModificationRequest;
import mokindang.jubging.project_backend.service.board.response.BoardIdResponse;
import mokindang.jubging.project_backend.service.board.response.BoardSelectionResponse;
import mokindang.jubging.project_backend.service.board.response.MultiBoardSelectResponse;
import mokindang.jubging.project_backend.web.argumentresolver.Login;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    ResponseEntity<BoardIdResponse> write(@Parameter(hidden = true) @Login Long memberId, @Valid @RequestBody final BoardCreationRequest boardCreationRequest);

    @Operation(summary = "게시글 조회", parameters = {
            @Parameter(name = "boardId", description = "Board 의 id", in = ParameterIn.PATH),
            @Parameter(name = AUTHORIZATION, description = "access token", in = ParameterIn.HEADER, required = true),
            @Parameter(name = SET_COOKIE, description = "refreshToken", in = ParameterIn.COOKIE, required = true)}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "글 조회"),
            @ApiResponse(responseCode = "400", description = "지역이 유효하지 않는 유저 \t\n" +
                    "존재하지 않는 게시글 조회", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    }
    )
    @GetMapping("/{boardId}")
    ResponseEntity<BoardSelectionResponse> selectBoard(@Parameter(hidden = true) @Login Long memberId, @PathVariable final Long boardId);

    @Operation(summary = "전체 게시글 리스트 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전체 게시글 리스트 조회 완료"),
    })
    @GetMapping
    ResponseEntity<MultiBoardSelectResponse> selectBoards(final Pageable pageable);

    @Operation(summary = "게시글 삭제", parameters = {
            @Parameter(name = "boardId", description = "삭제할 Board 의 Id", in = ParameterIn.PATH),
            @Parameter(name = AUTHORIZATION, description = "access token", in = ParameterIn.HEADER, required = true),
            @Parameter(name = SET_COOKIE, description = "refreshToken", in = ParameterIn.COOKIE, required = true)})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 삭제 완료",
                    content = @Content(schema = @Schema(implementation = BoardIdResponse.class))),
            @ApiResponse(responseCode = "400", description = "존재하지 않는 게시글에 대한 삭제 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "삭제 권한이 없는 회원의 삭제 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{boardId}")
    ResponseEntity<BoardIdResponse> delete(@Parameter(hidden = true) @Login final Long memberId, @PathVariable final Long boardId);

    @Operation(summary = "게시글 수정", parameters = {
            @Parameter(name = "boardId", description = "수정할 Board 의 Id", in = ParameterIn.PATH),
            @Parameter(name = AUTHORIZATION, description = "access token", in = ParameterIn.HEADER, required = true),
            @Parameter(name = SET_COOKIE, description = "refreshToken", in = ParameterIn.COOKIE, required = true)})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 수정 완료",
                    content = @Content(schema = @Schema(implementation = BoardIdResponse.class))),
            @ApiResponse(responseCode = "400", description = "존재하지 않는 게시글에 대한 수정 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = " 권한이 없는 회원의 수정 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{boardId}")
    ResponseEntity<BoardIdResponse> modifyBoard(@Parameter(hidden = true) @Login final Long memberId, @PathVariable final Long boardId,
                                                @RequestBody final BoardModificationRequest modificationRequest);

    @Operation(summary = "지역 게시글 리스트 게시글 조회", parameters = {
            @Parameter(name = AUTHORIZATION, description = "access token", in = ParameterIn.HEADER, required = true),
            @Parameter(name = SET_COOKIE, description = "refreshToken", in = ParameterIn.COOKIE, required = true)}
    )
    @GetMapping("/my-region")
    ResponseEntity<MultiBoardSelectResponse> selectRegionBoards(@Login final Long memberId, final Pageable pageable);
}
