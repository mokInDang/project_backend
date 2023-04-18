package mokindang.jubging.project_backend.controller.board.certificationboard;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mokindang.jubging.project_backend.service.board.certificationboard.CertificationBoardService;
import mokindang.jubging.project_backend.service.board.certificationboard.request.CertificationBoardCreationRequest;
import mokindang.jubging.project_backend.service.board.certificationboard.response.CertificationBoardIdResponse;
import mokindang.jubging.project_backend.service.board.certificationboard.response.CertificationBoardSelectionResponse;
import mokindang.jubging.project_backend.service.board.certificationboard.response.MultiCertificationBoardSelectResponse;
import mokindang.jubging.project_backend.web.argumentresolver.Login;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RequestMapping("/api/boards/certification")
@RestController
@RequiredArgsConstructor
public class CertificationBoardController implements CertificationBoardControllerSwagger{

    private final CertificationBoardService certificationBoardService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CertificationBoardIdResponse> write(@Login final Long memberId, @Valid @ModelAttribute CertificationBoardCreationRequest certificationBoardCreationRequest) {
        log.info("memberId = {} 의 인증 게시글 작성", memberId);
        CertificationBoardIdResponse certificationBoardIdResponse = certificationBoardService.write(memberId, certificationBoardCreationRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(certificationBoardIdResponse);
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<CertificationBoardSelectionResponse> selectBoard(@Login final Long memberId, @PathVariable final Long boardId) {
        log.info("memberId = {} 의 게시글 조회 요청, 게시글 번호 : {}", memberId, boardId);
        CertificationBoardSelectionResponse certificationBoardSelectionResponse = certificationBoardService.select(memberId, boardId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(certificationBoardSelectionResponse);
    }

    @GetMapping
    public ResponseEntity<MultiCertificationBoardSelectResponse> selectBoards(final Pageable pageable) {
        MultiCertificationBoardSelectResponse multiCertificationBoardSelectResponse = certificationBoardService.selectAllBoards(pageable);
        return ResponseEntity.ok()
                .body(multiCertificationBoardSelectResponse);
    }
}
