package mokindang.jubging.project_backend.controller.board;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "게시판", description = "게시판 관련 api")
@RequestMapping("/api/boards")
@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @Operation(summary = "새글작성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "새글작성"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 유저"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 본문내용"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 제목")
    })
    @PostMapping
    public ResponseEntity<Void> write(@Login Long memberId, @RequestBody final BoardCreateRequest boardCreateRequest) {
        log.info("memberId = {} 의 새글작성", memberId);
        boardService.write(memberId, boardCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }
}
