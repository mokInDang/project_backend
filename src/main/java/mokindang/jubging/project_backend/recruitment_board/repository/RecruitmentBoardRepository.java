package mokindang.jubging.project_backend.recruitment_board.repository;

import mokindang.jubging.project_backend.member.domain.vo.Region;
import mokindang.jubging.project_backend.recruitment_board.domain.RecruitmentBoard;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface RecruitmentBoardRepository extends JpaRepository<RecruitmentBoard, Long> {

    @Modifying
    @Query("UPDATE RecruitmentBoard b " +
            "SET b.onRecruitment = false" +
            " WHERE b.onRecruitment IS true " +
            "AND b.startingDate.startingDate < :today")
    void updateOnRecruitmentByStartingDate(LocalDate today);

    @BatchSize(size = 1000)
    @Query("SELECT b FROM RecruitmentBoard b ORDER BY b.creatingDateTime ASC")
    Slice<RecruitmentBoard> selectBoards(final Pageable pageable);

    @BatchSize(size = 1000)
    @Query("SELECT b " +
            "FROM RecruitmentBoard b " +
            "WHERE b.writingRegion = :region " +
            "ORDER BY b.creatingDateTime DESC")
    Slice<RecruitmentBoard> selectRegionBoards(final Region region, final Pageable pageable);


    @Query("SELECT b " +
            "FROM RecruitmentBoard  b " +
            "WHERE b.writingRegion = :region " +
            "AND b.onRecruitment = true " +
            "ORDER By b.startingDate.startingDate ASC")
    Slice<RecruitmentBoard> selectRecruitmentRegionBoardsCloseToDeadline(final Region region, final Pageable pageable);
    
}
