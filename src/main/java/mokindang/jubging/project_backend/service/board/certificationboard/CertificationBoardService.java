package mokindang.jubging.project_backend.service.board.certificationboard;

import lombok.RequiredArgsConstructor;
import mokindang.jubging.project_backend.domain.board.certificationboard.CertificationBoard;
import mokindang.jubging.project_backend.domain.member.Member;
import mokindang.jubging.project_backend.repository.board.certificationboard.CertificationBoardRepository;
import mokindang.jubging.project_backend.service.board.certificationboard.request.CertificationBoardCreationRequest;
import mokindang.jubging.project_backend.service.board.certificationboard.response.CertificationBoardIdResponse;
import mokindang.jubging.project_backend.service.file.FileService;
import mokindang.jubging.project_backend.service.member.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CertificationBoardService {

    private final MemberService memberService;
    private final FileService fileService;
    private final CertificationBoardRepository certificationBoardRepository;

    @Transactional
    public CertificationBoardIdResponse write(final Long memberId, final CertificationBoardCreationRequest certificationBoardCreationRequest) {
        Member writer = memberService.findByMemberId(memberId);
        LocalDateTime now = LocalDateTime.now();
        CertificationBoard certificationBoard = new CertificationBoard(now, now, writer, certificationBoardCreationRequest.getTitle(), certificationBoardCreationRequest.getContent());
        CertificationBoard savedCertificationBoard = certificationBoardRepository.save(certificationBoard);
        fileService.uploadFiles(certificationBoardCreationRequest.getFiles(), writer, savedCertificationBoard);
        return new CertificationBoardIdResponse(savedCertificationBoard.getId());
    }
}
