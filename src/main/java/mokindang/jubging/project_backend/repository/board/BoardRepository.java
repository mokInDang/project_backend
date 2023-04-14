package mokindang.jubging.project_backend.repository.board;

import mokindang.jubging.project_backend.domain.board.RecruitmentBoard;
import mokindang.jubging.project_backend.domain.member.vo.Region;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface BoardRepository extends JpaRepository<RecruitmentBoard, Long> {

    @Modifying
    @Query("UPDATE RecruitmentBoard b SET b.onRecruitment = false WHERE b.onRecruitment IS true AND b.startingDate.startingDate < :today")
    void updateOnRecruitmentByStartingDate(LocalDate today);

    @BatchSize(size = 1000)
    @Query("SELECT b FROM RecruitmentBoard b")
    Slice<RecruitmentBoard> selectBoards(final Pageable pageable);

    @BatchSize(size = 1000)
    @Query("SELECT b FROM RecruitmentBoard b WHERE b.writingRegion = :region ORDER BY b.creatingDateTime DESC")
    Slice<RecruitmentBoard> selectRegionBoards(final Region region, final Pageable pageable);
}
