package mokindang.jubging.project_backend.service.board.certificationboard;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mokindang.jubging.project_backend.domain.board.certificationboard.CertificationBoard;
import mokindang.jubging.project_backend.domain.member.Member;
import mokindang.jubging.project_backend.repository.board.certificationboard.CertificationBoardRepository;
import mokindang.jubging.project_backend.service.board.certificationboard.request.CertificationBoardCreationRequest;
import mokindang.jubging.project_backend.service.board.certificationboard.response.CertificationBoardIdResponse;
import mokindang.jubging.project_backend.service.board.certificationboard.response.CertificationBoardResponse;
import mokindang.jubging.project_backend.service.board.certificationboard.response.CertificationBoardSelectionResponse;
import mokindang.jubging.project_backend.service.board.certificationboard.response.MultiCertificationBoardSelectResponse;
import mokindang.jubging.project_backend.service.file.FileService;
import mokindang.jubging.project_backend.service.image.ImageService;
import mokindang.jubging.project_backend.service.member.MemberService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CertificationBoardService {

    private final MemberService memberService;
    private final FileService fileService;
    private final ImageService imageService;
    private final CertificationBoardRepository certificationBoardRepository;

    @Transactional
    public CertificationBoardIdResponse write(final Long memberId, final CertificationBoardCreationRequest certificationBoardCreationRequest) {
        Member writer = memberService.findByMemberId(memberId);
        LocalDateTime now = LocalDateTime.now();
        CertificationBoard certificationBoard = new CertificationBoard(now, now, writer, certificationBoardCreationRequest.getTitle(), certificationBoardCreationRequest.getContentBody());
        CertificationBoard savedCertificationBoard = certificationBoardRepository.save(certificationBoard);
        fileService.uploadFiles(certificationBoardCreationRequest.getFiles(), writer, savedCertificationBoard);
        return new CertificationBoardIdResponse(savedCertificationBoard.getId());
    }

    public CertificationBoardSelectionResponse select(final Long memberId, final Long boardId) {
        CertificationBoard certificationBoard = findById(boardId);
        List<String> findImagesUrl = imageService.findImagesUrl(certificationBoard);
        return new CertificationBoardSelectionResponse(certificationBoard, findImagesUrl, certificationBoard.isSameWriterId(memberId));
    }

    private CertificationBoard findById(final Long boardId) {
        return certificationBoardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다."));
    }

    public MultiCertificationBoardSelectResponse selectAllBoards(final Pageable pageable) {
        Slice<CertificationBoard> certificationBoards = certificationBoardRepository.selectBoards(pageable);
        List<CertificationBoardResponse> certificationBoardResponses = certificationBoards.stream()
                .map(CertificationBoardResponse::new)
                .collect(Collectors.toUnmodifiableList());
        return new MultiCertificationBoardSelectResponse(certificationBoardResponses, certificationBoards.hasNext());
    }
}
