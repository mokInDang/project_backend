package mokindang.jubging.project_backend.controller.board;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mokindang.jubging.project_backend.service.board.BoardService;
import mokindang.jubging.project_backend.service.board.request.BoardCreateRequest;
import mokindang.jubging.project_backend.service.board.response.BoardIdResponse;
import mokindang.jubging.project_backend.service.board.response.BoardSelectResponse;
import mokindang.jubging.project_backend.service.board.response.MultiBoardSelectResponse;
import mokindang.jubging.project_backend.web.argumentresolver.Login;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RequestMapping("/api/boards")
@RestController
@RequiredArgsConstructor
public class BoardController implements BoardControllerSwagger {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<BoardIdResponse> write(@Login Long memberId, @Valid @RequestBody final BoardCreateRequest boardCreateRequest) {
        log.info("memberId = {} 의 새글작성", memberId);
        BoardIdResponse boardIdResponse = boardService.write(memberId, boardCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(boardIdResponse);
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardSelectResponse> selectBoard(@Login final Long memberId, @PathVariable final Long boardId) {
        log.info("memberId = {} 의 게시글 조회 요청, 게시글 번호 : {}", memberId, boardId);
        BoardSelectResponse boardSelectResponse = boardService.select(memberId, boardId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(boardSelectResponse);
    }

    @GetMapping
    public ResponseEntity<MultiBoardSelectResponse> selectBoards(final Pageable pageable) {
        MultiBoardSelectResponse multiBoardSelectResponse = boardService.selectAllBoards(pageable);
        return ResponseEntity.ok()
                .body(multiBoardSelectResponse);}

    @DeleteMapping("/{boardId}")
    public ResponseEntity<BoardIdResponse> delete(@Login final Long memberId, @PathVariable final Long boardId) {
        log.info("memberId = {} 의 게시글 삭제 요청, 게시글 번호 : {}", memberId, boardId);
        BoardIdResponse deletedBoardId = boardService.delete(memberId, boardId);
        return ResponseEntity.ok(deletedBoardId);
    }

    @PatchMapping("/{boardId}")
    public ResponseEntity<BoardIdResponse> modifyBoard(@Login final Long memberId, @PathVariable final Long boardId) {
        log.info("memberId = {} 의 게시글 수정 요청, 게시글 번호 : {}", memberId, boardId);
        return null;
    }
}
