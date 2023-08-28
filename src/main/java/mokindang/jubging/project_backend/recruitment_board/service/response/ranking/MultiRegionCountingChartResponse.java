package mokindang.jubging.project_backend.recruitment_board.service.response.ranking;

import lombok.Getter;

import java.util.List;

@Getter
public class MultiRegionCountingChartResponse {

    private List<RegionCountingChartResponse> regions;

    private boolean hasNext;

    public MultiRegionCountingChartResponse(final List<RegionCountingChartResponse> regions, final boolean hasNext) {
        this.regions = regions;
        this.hasNext = hasNext;
    }
}
