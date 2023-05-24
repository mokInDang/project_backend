package mokindang.jubging.project_backend.recruitment_board.service.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class MultiRegionCountingChartResponse {

    private final List<RegionCountingChartResponse> regions;

    private final boolean hasNext;
}
