package mokindang.jubging.project_backend.service.board;

import lombok.RequiredArgsConstructor;
import mokindang.jubging.project_backend.repository.board.BoardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BoardSchedulingService {

    private final BoardRepository boardRepository;

    @Transactional
    public void updateOnRecruitmentByStartingDate() {
        LocalDate today = LocalDate.now();
        boardRepository.updateOnRecruitmentByStartingDate(today);
    }
}
