package mokindang.jubging.project_backend.recruitment_board.service;

import mokindang.jubging.project_backend.recruitment_board.domain.RecruitmentBoard;
import mokindang.jubging.project_backend.recruitment_board.repository.RecruitmentBoardRepository;
import mokindang.jubging.project_backend.recruitment_board.service.facade.OptimisticLockRecruitmentBoardFacade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RecruitmentBoardConcurrencyTest {

    @Autowired
    private OptimisticLockRecruitmentBoardFacade recruitmentBoardService;

    @Autowired
    private RecruitmentBoardRepository recruitmentBoardRepository;

    @Test
    @DisplayName("동시성 문제  - 8명이 동시에 요청한 경우 게시글 카운트가 8이됨")
    void SuccessCountUp() throws InterruptedException {
        int threadCount = 7;
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            int memberId = i + 2;
            executorService.submit(() -> {
                try {
                    recruitmentBoardService.participate((long) memberId, 1L);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                } finally {
                    latch.countDown(); // 카운트
                }
            });
        }
        latch.await();

        RecruitmentBoard recruitmentBoard = recruitmentBoardRepository.findById(1L).get();
        assertThat(recruitmentBoard.getParticipationCount().getCount()).isEqualTo(8);
    }
}
