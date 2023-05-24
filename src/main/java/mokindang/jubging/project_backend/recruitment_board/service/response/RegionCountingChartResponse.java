package mokindang.jubging.project_backend.recruitment_board.service.response;

import lombok.Getter;
import mokindang.jubging.project_backend.member.domain.vo.Region;

@Getter
public class RegionCountingChartResponse {

    private final String region;

    public RegionCountingChartResponse(Region region) {
        this.region = region.getValue();
    }
}
