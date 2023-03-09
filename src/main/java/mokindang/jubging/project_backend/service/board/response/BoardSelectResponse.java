package mokindang.jubging.project_backend.service.board.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import mokindang.jubging.project_backend.domain.board.ActivityCategory;
import mokindang.jubging.project_backend.domain.board.vo.StartingDate;
import mokindang.jubging.project_backend.domain.region.vo.Region;

@Getter
@AllArgsConstructor
public class BoardSelectResponse {

    private Long boardId;
    private String title;
    private String content;
    private String writerAlias;
    private StartingDate startingDate;
    private Region region;
    private ActivityCategory activityCategory;
    private boolean onRecruitment;
}
