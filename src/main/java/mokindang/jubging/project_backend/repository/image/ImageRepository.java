package mokindang.jubging.project_backend.repository.image;

import mokindang.jubging.project_backend.domain.image.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
