package mokindang.jubging.project_backend.image.repository;

import mokindang.jubging.project_backend.certification_board.domain.CertificationBoard;
import mokindang.jubging.project_backend.image.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {

    Optional<List<Image>> findByCertificationBoard(CertificationBoard certificationBoard);

    void deleteByStoreName(String storeName);
}
