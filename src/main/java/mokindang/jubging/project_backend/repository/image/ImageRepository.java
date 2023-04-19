package mokindang.jubging.project_backend.repository.image;

import mokindang.jubging.project_backend.domain.board.certificationboard.CertificationBoard;
import mokindang.jubging.project_backend.domain.image.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {

    Optional<List<Image>> findByCertificationBoard(CertificationBoard certificationBoard);
}
