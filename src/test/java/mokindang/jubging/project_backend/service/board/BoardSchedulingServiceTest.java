package mokindang.jubging.project_backend.service.board;

import mokindang.jubging.project_backend.repository.board.BoardRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardSchedulingServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @InjectMocks
    private BoardSchedulingService boardSchedulingService;

    @Test
    @DisplayName("게시글 활동 여부가 true 인 것들 중, 활동 시작일이 현재 시간보다 과거에 있으면 활동 여부를 false 로 변경한다.")
    void updateOnRecruitmentByStartingDate() {
        //when
        boardSchedulingService.updateOnRecruitmentByStartingDate();

        //then
        verify(boardRepository, times(1)).updateOnRecruitmentByStartingDate(any());
    }
}
