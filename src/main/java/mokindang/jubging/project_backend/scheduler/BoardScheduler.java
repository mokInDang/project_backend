package mokindang.jubging.project_backend.scheduler;

import lombok.RequiredArgsConstructor;
import mokindang.jubging.project_backend.service.board.BoardSchedulingService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BoardScheduler {

    private final BoardSchedulingService boardSchedulingService;

    private static final String EVERY_MIDNIGHT = "0 0 0 * * *";

    @Scheduled(cron = EVERY_MIDNIGHT)
    public void updateOnRecruitment() {
        boardSchedulingService.updateOnRecruitmentByStartingDate();
    }
}
