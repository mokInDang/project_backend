package mokindang.jubging.project_backend.controller.board;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mokindang.jubging.project_backend.service.board.BoardService;
import mokindang.jubging.project_backend.service.board.request.BoardCreateRequest;
import mokindang.jubging.project_backend.service.board.response.BoardIdResponse;
import mokindang.jubging.project_backend.service.board.response.BoardSelectResponse;
import mokindang.jubging.project_backend.web.argumentresolver.Login;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RequestMapping("/api/boards")
@RestController
@RequiredArgsConstructor
public class BoardController implements BoardControllerSwagger{

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
        log.info("memberId = [} 의 게시글 조회, 게시글 번호 : {}", memberId, boardId);
        BoardSelectResponse boardSelectResponse = boardService.select(memberId, boardId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(boardSelectResponse);
    }
}
