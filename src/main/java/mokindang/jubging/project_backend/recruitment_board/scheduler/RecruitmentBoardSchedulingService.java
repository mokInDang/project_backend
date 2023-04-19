package mokindang.jubging.project_backend.recruitment_board.scheduler;

import lombok.RequiredArgsConstructor;
import mokindang.jubging.project_backend.recruitment_board.repository.RecruitmentBoardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RecruitmentBoardSchedulingService {

    private final RecruitmentBoardRepository recruitmentBoardRepository;

    @Transactional
    public void updateOnRecruitmentByStartingDate() {
        LocalDate today = LocalDate.now();
        recruitmentBoardRepository.updateOnRecruitmentByStartingDate(today);
    }
}
