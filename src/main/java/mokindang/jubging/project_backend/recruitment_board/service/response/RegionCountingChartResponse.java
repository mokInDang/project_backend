package mokindang.jubging.project_backend.recruitment_board.service.response;

import lombok.Getter;

@Getter
public class RegionCountingChartResponse {

    private String region;

    public RegionCountingChartResponse(String region) {
        this.region = region;
    }
}
