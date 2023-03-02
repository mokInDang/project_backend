package mokindang.jubging.project_backend.service.member.request;

import lombok.Getter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@Getter
public class RegionRequest {

    @NotNull
    @Range(min = 124, max = 132, message = "longitude(경도)는 대한민국 영토 범위 124~132 내의 값이어야 합니다.")
    private final Double longitude;

    @NotNull
    @Range(min = 33, max = 39, message = "latitude(위도)는 대한민국 영토 범위 33~39 내의 값이어야 합니다.")
    private final Double latitude;

    public RegionRequest(Double longitude, Double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }
}