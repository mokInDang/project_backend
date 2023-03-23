package mokindang.jubging.project_backend.service.board;

import lombok.RequiredArgsConstructor;
import mokindang.jubging.project_backend.domain.board.Board;
import mokindang.jubging.project_backend.repository.board.BoardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardSchedulingService {

    private final Clock clock;
    private final BoardRepository boardRepository;

    @Transactional
    public void updateOnRecruitmentByStartingDate() {
        LocalDate now = LocalDate.now(clock);

        List<Board> boardsByOnRecruitmentTrue = boardRepository.findBoardsByOnRecruitmentTrue();
        boardsByOnRecruitmentTrue.forEach(board -> board.updateOnRecruitmentByStaringDate(now));
    }
}
