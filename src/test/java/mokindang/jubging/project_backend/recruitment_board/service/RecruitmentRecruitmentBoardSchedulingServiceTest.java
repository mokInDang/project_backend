package mokindang.jubging.project_backend.recruitment_board.service;

import mokindang.jubging.project_backend.recruitment_board.repository.RecruitmentBoardRepository;
import mokindang.jubging.project_backend.recruitment_board.scheduler.RecruitmentBoardSchedulingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecruitmentRecruitmentBoardSchedulingServiceTest {

    @Mock
    private RecruitmentBoardRepository recruitmentBoardRepository;

    @InjectMocks
    private RecruitmentBoardSchedulingService recruitmentBoardSchedulingService;

    @Test
    @DisplayName("게시글 활동 여부가 true 인 것들 중, 활동 시작일이 현재 시간보다 과거에 있으면 활동 여부를 false 로 변경한다.")
    void updateOnRecruitmentByStartingDate() {
        //when
        recruitmentBoardSchedulingService.updateOnRecruitmentByStartingDate();

        //then
        verify(recruitmentBoardRepository, times(1)).updateOnRecruitmentByStartingDate(any());
    }
}
