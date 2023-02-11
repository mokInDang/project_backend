package mokindang.jubging.project_backend.controller.board;

import lombok.extern.slf4j.Slf4j;
import mokindang.jubging.project_backend.service.board.request.BoardCreateRequest;
import mokindang.jubging.project_backend.web.argumentresolver.Login;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController("/api/boards")
public class BoardController {

    @PostMapping
    public ResponseEntity<Void> write(@Login Long memberId, final BoardCreateRequest boardCreateRequest) {
        log.info("memberId = {} 의 새글작성", memberId);
        return null;
    }
}
