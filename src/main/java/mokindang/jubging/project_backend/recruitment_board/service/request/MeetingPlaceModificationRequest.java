package mokindang.jubging.project_backend.recruitment_board.service.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@RequiredArgsConstructor
public class MeetingPlaceModificationRequest {

    @NotNull
    @Schema(description = "활동 장소 경도 좌표", example = "12.12312412")
    private final Double longitude;

    @NotNull
    @Schema(description = "활동 장소 위도 좌표", example = "34.12312312")
    private final Double latitude;

    @NotNull
    @Schema(description = "활동 장소 상세 주소", example = "서울시 동작구 상도동 1-1")
    private final String meetingAddress;
}
