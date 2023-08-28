package mokindang.jubging.project_backend.recruitment_board.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mokindang.jubging.project_backend.exception.ErrorResponse;
import mokindang.jubging.project_backend.recruitment_board.service.request.BoardModificationRequest;
import mokindang.jubging.project_backend.recruitment_board.service.request.RecruitmentBoardCreationRequest;
import mokindang.jubging.project_backend.recruitment_board.service.response.*;
import mokindang.jubging.project_backend.recruitment_board.service.response.board.MultiBoardSelectionResponse;
import mokindang.jubging.project_backend.recruitment_board.service.response.board.RecruitmentBoardSelectionResponse;
import mokindang.jubging.project_backend.recruitment_board.service.response.marker.MultiBoardPlaceSelectionResponse;
import mokindang.jubging.project_backend.recruitment_board.service.response.ranking.MultiRegionCountingChartResponse;
import mokindang.jubging.project_backend.web.argumentresolver.Login;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Tag(name = "구인 게시판", description = "구인 게시판 관련 api")
public interface RecruitmentBoardControllerSwagger {

    @Operation(summary = "새글작성", parameters = {
            @Parameter(name = AUTHORIZATION, description = "access token", in = ParameterIn.HEADER, required = true)}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "새글작성"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 유저 \t\n" +
                    "유효하지 않은 본문내용 \t\n" +
                    "유효하지 않은 제목 \t\n" +
                    "유효하지 않은 활동 시작일", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping
    ResponseEntity<RecruitmentBoardIdResponse> write(@Parameter(hidden = true) @Login Long memberId, @Valid @RequestBody final RecruitmentBoardCreationRequest recruitmentBoardCreationRequest);

    @Operation(summary = "게시글 조회", parameters = {
            @Parameter(name = "boardId", description = "Board 의 id", in = ParameterIn.PATH),
            @Parameter(name = AUTHORIZATION, description = "access token", in = ParameterIn.HEADER, required = true),
    }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "글 조회"),
            @ApiResponse(responseCode = "400", description = "지역이 유효하지 않는 유저 \t\n" +
                    "존재하지 않는 게시글 조회", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    }
    )
    @GetMapping("/{boardId}")
    ResponseEntity<RecruitmentBoardSelectionResponse> selectBoard(@Parameter(hidden = true) @Login Long memberId, @PathVariable final Long boardId);

    @Operation(summary = "전체 게시글 리스트 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전체 게시글 리스트 조회 완료"),
    })
    @GetMapping
    ResponseEntity<MultiBoardSelectionResponse> selectBoards(final Pageable pageable);

    @Operation(summary = "게시글 삭제", parameters = {
            @Parameter(name = "boardId", description = "삭제할 Board 의 Id", in = ParameterIn.PATH),
            @Parameter(name = AUTHORIZATION, description = "access token", in = ParameterIn.HEADER, required = true),
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 삭제 완료",
                    content = @Content(schema = @Schema(implementation = RecruitmentBoardIdResponse.class))),
            @ApiResponse(responseCode = "400", description = "존재하지 않는 게시글에 대한 삭제 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "삭제 권한이 없는 회원의 삭제 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{boardId}")
    ResponseEntity<RecruitmentBoardIdResponse> delete(@Parameter(hidden = true) @Login final Long memberId, @PathVariable final Long boardId);

    @Operation(summary = "게시글 수정", parameters = {
            @Parameter(name = "boardId", description = "수정할 Board 의 Id", in = ParameterIn.PATH),
            @Parameter(name = AUTHORIZATION, description = "access token", in = ParameterIn.HEADER, required = true),
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 수정 완료",
                    content = @Content(schema = @Schema(implementation = RecruitmentBoardIdResponse.class))),
            @ApiResponse(responseCode = "400", description = "존재하지 않는 게시글에 대한 수정 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = " 권한이 없는 회원의 수정 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{boardId}")
    ResponseEntity<RecruitmentBoardIdResponse> modifyBoard(@Parameter(hidden = true) @Login final Long memberId, @PathVariable final Long boardId,
                                                           @RequestBody final BoardModificationRequest modificationRequest);

    @Operation(summary = "구인 게시글 참여 요청", parameters = {
            @Parameter(name = "boardId", description = "참여요청 할 Board 의 Id", in = ParameterIn.PATH),
            @Parameter(name = AUTHORIZATION, description = "access token", in = ParameterIn.HEADER, required = true),
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 참여 완료",
                    content = @Content(schema = @Schema(implementation = RecruitmentBoardIdResponse.class))),
            @ApiResponse(responseCode = "400", description = "모집이 마감된 게시글에 대한 참여 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "참여인원이 꽉 찬 게시글에 대한 참여 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "회원의 지역과 게시글의 지역이 다른 경우에 대한 참여 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{boardId}/participation-list")
    ResponseEntity<RecruitmentBoardIdResponse> participate(@Parameter(hidden = true) @Login final Long memberId, @PathVariable final Long boardId);

    @Operation(summary = "지역 게시글 리스트 게시글 조회", parameters = {
            @Parameter(name = AUTHORIZATION, description = "access token", in = ParameterIn.HEADER, required = true),
    }
    )
    @GetMapping("/my-region")
    ResponseEntity<MultiBoardSelectionResponse> selectRegionBoards(@Parameter(hidden = true) @Login final Long memberId, final Pageable pageable);

    @Operation(summary = "장소 마커 리스트 조회", parameters = {
            @Parameter(name = AUTHORIZATION, description = "access token", in = ParameterIn.HEADER, required = true),
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 마커 조회 완료",
                    content = @Content(schema = @Schema(implementation = MultiBoardPlaceSelectionResponse.class))),
            @ApiResponse(responseCode = "403", description = " 권한이 없는 회원의 조회 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/places")
    ResponseEntity<MultiBoardPlaceSelectionResponse> selectPlacesOfRegionBoards(@Parameter(hidden = true) @Login final Long memberId, final Pageable pageable);

    @Operation(summary = "모집 게시글 갯수 상위 5개 지역")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모집 게시글 갯수 상위 5개 지역 조회 완료"),
    })
    @GetMapping("/region-rank")
    ResponseEntity<MultiRegionCountingChartResponse> getRegionCountingChart(final Pageable pageable);
}
