package mokindang.jubging.project_backend.service.image;

import lombok.RequiredArgsConstructor;
import mokindang.jubging.project_backend.domain.board.certificationboard.CertificationBoard;
import mokindang.jubging.project_backend.domain.image.Image;
import mokindang.jubging.project_backend.repository.image.ImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    public List<String> findImagesUrl(final CertificationBoard board) {
        List<String> imagesUrl = new ArrayList<>();
        List<Image> images = imageRepository.findByCertificationBoard(board)
                .orElseThrow(() -> new IllegalArgumentException("인증 게시판 id로 저장된 이미지가 존재하지 않습니다."));

        for (Image image : images) {
            imagesUrl.add(image.getFilePath());
        }
        return imagesUrl;
    }
}
