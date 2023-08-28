package mokindang.jubging.project_backend.recruitment_board.service.response.marker;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mokindang.jubging.project_backend.recruitment_board.service.response.marker.BoardPlaceMarkerResponse;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class MultiBoardPlaceSelectionResponse {

    @Schema(description = "장소 리스트")
    private final List<BoardPlaceMarkerResponse> boardPlaceMarkerResponses;

    @Schema(description = "다음 장소 리스트 존재 여부")
    private final boolean hasNext;

}
