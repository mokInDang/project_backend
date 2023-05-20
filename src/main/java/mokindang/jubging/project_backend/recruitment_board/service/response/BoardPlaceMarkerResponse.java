package mokindang.jubging.project_backend.recruitment_board.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import mokindang.jubging.project_backend.recruitment_board.domain.RecruitmentBoard;

@Getter
public class BoardPlaceMarkerResponse {

    @Schema(description = "게시글 id")
    private final RecruitmentBoardIdResponse recruitmentBoardIdResponse;

    @Schema(description = "게시글 장소 정보")
    private final MeetingPlaceResponse meetingPlaceResponse;

    @Schema(description = "게시글 제목")
    private final String title;

    public BoardPlaceMarkerResponse(final RecruitmentBoard recruitmentBoard) {
        recruitmentBoardIdResponse = new RecruitmentBoardIdResponse(recruitmentBoard.getId());
        meetingPlaceResponse = new MeetingPlaceResponse(recruitmentBoard);
        title = recruitmentBoard.getTitle().getValue();
    }
}
