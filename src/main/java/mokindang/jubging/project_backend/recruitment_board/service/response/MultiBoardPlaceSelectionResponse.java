package mokindang.jubging.project_backend.recruitment_board.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
public class MultiBoardPlaceSelectionResponse {

    @Schema(description = "장소 리스트")
    List<BoardPlaceMarkerResponse> boardPlaceMarkerResponses;

    public MultiBoardPlaceSelectionResponse(final List<BoardPlaceMarkerResponse> boards) {
        this.boardPlaceMarkerResponses = boards;
    }
}
