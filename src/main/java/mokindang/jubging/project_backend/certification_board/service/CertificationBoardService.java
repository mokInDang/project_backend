package mokindang.jubging.project_backend.certification_board.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mokindang.jubging.project_backend.certification_board.domain.CertificationBoard;
import mokindang.jubging.project_backend.member.domain.Member;
import mokindang.jubging.project_backend.certification_board.repository.CertificationBoardRepository;
import mokindang.jubging.project_backend.image.repository.ImageRepository;
import mokindang.jubging.project_backend.certification_board.service.request.CertificationBoardCreationRequest;
import mokindang.jubging.project_backend.certification_board.service.request.CertificationBoardModificationRequest;
import mokindang.jubging.project_backend.certification_board.service.response.CertificationBoardIdResponse;
import mokindang.jubging.project_backend.certification_board.service.response.CertificationBoardResponse;
import mokindang.jubging.project_backend.certification_board.service.response.CertificationBoardSelectionResponse;
import mokindang.jubging.project_backend.certification_board.service.response.MultiCertificationBoardSelectResponse;
import mokindang.jubging.project_backend.file.FileService;
import mokindang.jubging.project_backend.image.service.ImageService;
import mokindang.jubging.project_backend.member.service.MemberService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static mokindang.jubging.project_backend.file.FileService.CERTIFICATION_BOARD_IMAGE;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CertificationBoardService {

    private final ImageRepository imageRepository;
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

    public CertificationBoard findById(final Long boardId) {
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

    @Transactional
    public CertificationBoardIdResponse delete(final Long memberId, final Long boardId) {
        CertificationBoard certificationBoard = findById(boardId);
        certificationBoard.validatePermission(memberId);
        deleteImage(certificationBoard);
        certificationBoardRepository.delete(certificationBoard);
        return new CertificationBoardIdResponse(certificationBoard.getId());
    }

    private void deleteImage(CertificationBoard certificationBoard) {
        List<String> imagesName = imageService.findImagesName(certificationBoard);
        for (String imageName : imagesName) {
            fileService.deleteFile(imageName, CERTIFICATION_BOARD_IMAGE);
            imageRepository.deleteByStoreName(imageName);
        }
    }

    @Transactional
    public CertificationBoardIdResponse modify(final Long memberId, final Long boardId, final CertificationBoardModificationRequest certificationBoardModificationRequest) {
        Member writer = memberService.findByMemberId(memberId);
        CertificationBoard certificationBoard = findById(boardId);
        deleteImage(certificationBoard);
        fileService.uploadFiles(certificationBoardModificationRequest.getFiles(), writer, certificationBoard);
        certificationBoard.modify(memberId, certificationBoardModificationRequest.getTitle(), certificationBoardModificationRequest.getContentBody());
        return new CertificationBoardIdResponse(certificationBoard.getId());
    }
}
