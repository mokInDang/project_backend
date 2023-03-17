package mokindang.jubging.project_backend.repository.board;

import mokindang.jubging.project_backend.domain.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    Optional<Board> findById(Long id);
}
