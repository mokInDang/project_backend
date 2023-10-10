package mokindang.jubging.project_backend.recruitment_board.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mokindang.jubging.project_backend.recruitment_board.service.RecruitmentBoardService;
import mokindang.jubging.project_backend.recruitment_board.service.facade.OptimisticLockRecruitmentBoardResolver;
import mokindang.jubging.project_backend.recruitment_board.service.request.BoardModificationRequest;
import mokindang.jubging.project_backend.recruitment_board.service.request.RecruitmentBoardCreationRequest;
import mokindang.jubging.project_backend.recruitment_board.service.response.RecruitmentBoardIdResponse;
import mokindang.jubging.project_backend.recruitment_board.service.response.board.MultiBoardSelectionResponse;
import mokindang.jubging.project_backend.recruitment_board.service.response.board.RecruitmentBoardSelectionResponse;
import mokindang.jubging.project_backend.recruitment_board.service.response.marker.MultiBoardPlaceSelectionResponse;
import mokindang.jubging.project_backend.recruitment_board.service.response.ranking.MultiRegionCountingChartResponse;
import mokindang.jubging.project_backend.web.argumentresolver.Login;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RequestMapping("/api/boards/recruitment")
@RestController
@RequiredArgsConstructor
public class RecruitmentBoardController implements RecruitmentBoardControllerSwagger {

    private final RecruitmentBoardService recruitmentBoardService;
    private final OptimisticLockRecruitmentBoardResolver optimisticLockRecruitmentBoardResolver;

    @PostMapping
    public ResponseEntity<RecruitmentBoardIdResponse> write(@Login Long memberId, @Valid @RequestBody final RecruitmentBoardCreationRequest recruitmentBoardCreationRequest) {
        log.info("memberId = {} 의 새글작성", memberId);
        RecruitmentBoardIdResponse recruitmentBoardIdResponse = recruitmentBoardService.write(memberId, recruitmentBoardCreationRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recruitmentBoardIdResponse);
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<RecruitmentBoardSelectionResponse> selectBoard(@Login final Long memberId, @PathVariable final Long boardId) {
        log.info("memberId = {} 의 게시글 조회 요청, 게시글 번호 : {}", memberId, boardId);
        RecruitmentBoardSelectionResponse recruitmentBoardSelectionResponse = recruitmentBoardService.select(memberId, boardId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(recruitmentBoardSelectionResponse);
    }

    @GetMapping
    public ResponseEntity<MultiBoardSelectionResponse> selectBoards(@RequestParam(name = "boardId") final Long startId, final Pageable pageable) {
        MultiBoardSelectionResponse multiBoardSelectionResponse = recruitmentBoardService.selectAllBoards(startId, pageable);
        return ResponseEntity.ok()
                .body(multiBoardSelectionResponse);
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<RecruitmentBoardIdResponse> delete(@Login final Long memberId, @PathVariable final Long boardId) {
        log.info("memberId = {} 의 게시글 삭제 요청, 게시글 번호 : {}", memberId, boardId);
        RecruitmentBoardIdResponse deletedBoardId = recruitmentBoardService.delete(memberId, boardId);
        return ResponseEntity.ok(deletedBoardId);
    }

    @PatchMapping("/{boardId}")
    public ResponseEntity<RecruitmentBoardIdResponse> modifyBoard(@Login final Long memberId, @PathVariable final Long boardId, @RequestBody final BoardModificationRequest modificationRequest) {
        log.info("memberId = {} 의 게시글 수정 요청, 게시글 번호 : {}", memberId, boardId);
        RecruitmentBoardIdResponse recruitmentBoardIdResponse = recruitmentBoardService.modify(memberId, boardId, modificationRequest);
        return ResponseEntity.ok()
                .body(recruitmentBoardIdResponse);
    }

    @PatchMapping("/{boardId}/participation-list")
    public ResponseEntity<RecruitmentBoardIdResponse> participate(@Login final Long memberId, @PathVariable final Long boardId) {
        log.info(("memberId = {} 의 플로깅 참여 요청, 게시글 번호 : {}"), memberId, boardId);
        RecruitmentBoardIdResponse recruitmentBoardIdResponse = optimisticLockRecruitmentBoardResolver.participate(memberId, boardId);
        return ResponseEntity.ok()
                .body(recruitmentBoardIdResponse);
    }

    @PatchMapping("/{boardId}/recruitment-status")
    public ResponseEntity<RecruitmentBoardIdResponse> closeRecruitment(@Login final Long memberId, @PathVariable final Long boardId) {
        log.info("memberId = {} 의 게시글 모집 마감 요청, 게시글 번호 : {}", memberId, boardId);
        RecruitmentBoardIdResponse recruitmentBoardIdResponse = recruitmentBoardService.closeRecruitment(memberId, boardId);
        return ResponseEntity.ok().body(recruitmentBoardIdResponse);
    }

    @GetMapping("/region")
    public ResponseEntity<MultiBoardSelectionResponse> selectRegionBoards(@Login final Long memberId, final Pageable pageable) {
        MultiBoardSelectionResponse multiBoardSelectionResponse = recruitmentBoardService.selectRegionBoards(memberId, pageable);
        return ResponseEntity.ok()
                .body(multiBoardSelectionResponse);
    }

    @GetMapping("/places")
    public ResponseEntity<MultiBoardPlaceSelectionResponse> selectPlacesOfRegionBoards(@Login final Long memberId, final Pageable pageable) {
        MultiBoardPlaceSelectionResponse multiBoardPlaceSelectionResponse = recruitmentBoardService.selectRegionBoardsCloseToDeadline(memberId, pageable);
        return ResponseEntity.ok()
                .body(multiBoardPlaceSelectionResponse);
    }

    @GetMapping("/region-rank")
    public ResponseEntity<MultiRegionCountingChartResponse> getRegionCountingChart(final Pageable pageable) {
        MultiRegionCountingChartResponse regionCountingChart = recruitmentBoardService.getRegionCountingChart(pageable);
        return ResponseEntity.ok()
                .body(regionCountingChart);
    }
}
