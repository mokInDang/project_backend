package mokindang.jubging.project_backend.certification_board.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mokindang.jubging.project_backend.certification_board.service.CertificationBoardService;
import mokindang.jubging.project_backend.certification_board.service.request.CertificationBoardCreationRequest;
import mokindang.jubging.project_backend.certification_board.service.request.CertificationBoardModificationRequest;
import mokindang.jubging.project_backend.certification_board.service.response.CertificationBoardIdResponse;
import mokindang.jubging.project_backend.certification_board.service.response.CertificationBoardSelectionResponse;
import mokindang.jubging.project_backend.certification_board.service.response.MultiCertificationBoardSelectResponse;
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
        log.info("memberId = {} 의 인증 게시글 조회 요청, 인증 게시글 번호 : {}", memberId, boardId);
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

    @DeleteMapping("/{boardId}")
    public ResponseEntity<CertificationBoardIdResponse> delete(@Login final Long memberId, @PathVariable final Long boardId) {
        log.info("memberId = {} 의 인증 게시글 삭제 요청, 게시글 번호 : {}", memberId, boardId);
        CertificationBoardIdResponse deletedCertificationBoardId = certificationBoardService.delete(memberId, boardId);
        return ResponseEntity.ok(deletedCertificationBoardId);
    }

    @PatchMapping("/{boardId}")
    public ResponseEntity<CertificationBoardIdResponse> modifyBoard(@Login final Long memberId, @PathVariable final Long boardId, @ModelAttribute final CertificationBoardModificationRequest certificationBoardModificationRequest) {
        log.info("memberId = {} 의 게시글 수정 요청, 게시글 번호 : {}", memberId, boardId);
        CertificationBoardIdResponse certificationBoardIdResponse = certificationBoardService.modify(memberId, boardId, certificationBoardModificationRequest);
        return ResponseEntity.ok()
                .body(certificationBoardIdResponse);
    }
}
