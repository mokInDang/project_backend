package mokindang.jubging.project_backend.recruitment_board.service.response;

import lombok.Getter;
import mokindang.jubging.project_backend.recruitment_board.domain.RecruitmentBoard;

@Getter
public class BoardPlaceMarkerResponse {

    private final RecruitmentBoardIdResponse recruitmentBoardIdResponse;

    private final MeetingPlaceResponse meetingPlaceResponse;

    public BoardPlaceMarkerResponse(final RecruitmentBoard recruitmentBoard) {
        recruitmentBoardIdResponse = new RecruitmentBoardIdResponse(recruitmentBoard.getId());
        meetingPlaceResponse = new MeetingPlaceResponse(recruitmentBoard);
    }
}
