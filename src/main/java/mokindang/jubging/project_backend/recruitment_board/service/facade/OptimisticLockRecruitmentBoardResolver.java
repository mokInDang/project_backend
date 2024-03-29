package mokindang.jubging.project_backend.recruitment_board.service.facade;

import lombok.RequiredArgsConstructor;
import mokindang.jubging.project_backend.recruitment_board.service.RecruitmentBoardService;
import mokindang.jubging.project_backend.recruitment_board.service.response.RecruitmentBoardIdResponse;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OptimisticLockRecruitmentBoardResolver {

    private final RecruitmentBoardService recruitmentBoardService;

    public RecruitmentBoardIdResponse participate(final Long memberId, final Long boardId) {
        RecruitmentBoardIdResponse recruitmentBoardIdResponse;
        while (true) {
            try {
                recruitmentBoardIdResponse = recruitmentBoardService.participate(memberId, boardId);
                break;
            } catch (final ObjectOptimisticLockingFailureException e) {
                sleep();
            }
        }
        return recruitmentBoardIdResponse;
    }

    private void sleep() {
        try {
            Thread.sleep(50);
        } catch (final InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
