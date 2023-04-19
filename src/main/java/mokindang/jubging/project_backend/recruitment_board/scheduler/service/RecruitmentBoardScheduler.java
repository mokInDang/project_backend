package mokindang.jubging.project_backend.recruitment_board.scheduler.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mokindang.jubging.project_backend.recruitment_board.scheduler.RecruitmentBoardSchedulingService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecruitmentBoardScheduler {

    private final RecruitmentBoardSchedulingService recruitmentBoardSchedulingService;

    private static final String EVERY_MIDNIGHT = "0 0 0 * * *";

    @Scheduled(cron = EVERY_MIDNIGHT)
    public void updateOnRecruitment() {
        log.info("매일 자정 게시글 모집여부 업데이트 스케줄러 실행, 실행 시각 = {}", LocalDateTime.now());
        recruitmentBoardSchedulingService.updateOnRecruitmentByStartingDate();
    }
}
