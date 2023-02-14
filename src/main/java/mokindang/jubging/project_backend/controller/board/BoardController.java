package mokindang.jubging.project_backend.controller.board;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mokindang.jubging.project_backend.service.board.BoardService;
import mokindang.jubging.project_backend.service.board.request.BoardCreateRequest;
import mokindang.jubging.project_backend.web.argumentresolver.Login;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api/boards")
@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<Void> write(@Login Long memberId, @RequestBody final BoardCreateRequest boardCreateRequest) {
        log.info("memberId = {} 의 새글작성", memberId);
        boardService.write(memberId, boardCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }
}
