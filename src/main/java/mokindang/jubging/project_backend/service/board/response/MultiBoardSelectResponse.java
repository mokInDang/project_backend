package mokindang.jubging.project_backend.service.board.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class MultiBoardSelectResponse {

    private final List<SummaryBoardResponse> boards;
    private final boolean hasNext;
}
