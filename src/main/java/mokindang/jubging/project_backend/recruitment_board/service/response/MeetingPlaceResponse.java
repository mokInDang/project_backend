package mokindang.jubging.project_backend.recruitment_board.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import mokindang.jubging.project_backend.recruitment_board.domain.RecruitmentBoard;

@Getter
public class MeetingPlaceResponse {

    @Schema(description = "만남 장소 경도 값", example = "1.1")
    private final Double longitude;

    @Schema(description = "만남 장소 위도 값", example = "1.2")
    private final Double latitude;

    @Schema(description = "만남 장소 상세 주소", example = "서울시 동작구 상도동 1-1")
    private final String meetingAddress;

    public MeetingPlaceResponse(final RecruitmentBoard board) {
        this.longitude = board.getMeetingPlace()
                .getCoordinate().getPointOfLongitude();
        this.latitude = board.getMeetingPlace()
                .getCoordinate().getPointOfLatitude();
        this.meetingAddress = board.getMeetingPlace()
                .getAddress();
    }
}
