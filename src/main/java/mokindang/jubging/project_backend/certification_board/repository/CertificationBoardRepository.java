package mokindang.jubging.project_backend.certification_board.repository;

import mokindang.jubging.project_backend.certification_board.domain.CertificationBoard;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CertificationBoardRepository extends JpaRepository<CertificationBoard, Long> {

    @BatchSize(size = 1000)
    @Query("SELECT c FROM CertificationBoard c")
    Slice<CertificationBoard> selectBoards(final Pageable pageable);
}
