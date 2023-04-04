package mokindang.jubging.project_backend.repository.board;

import mokindang.jubging.project_backend.domain.board.Board;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Modifying
    @Query("UPDATE Board b SET b.onRecruitment = false WHERE b.onRecruitment IS true AND b.startingDate.startingDate < :today")
    void updateOnRecruitmentByStartingDate(LocalDate today);

    @BatchSize(size = 1000)
    @Query("SELECT b FROM Board b")
    Slice<Board> selectBoards(final Pageable pageable);
}
