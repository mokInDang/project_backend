package mokindang.jubging.project_backend.service.board.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoardSelectResponse {

    private Long boardId;
    private String title;
    private String content;
    private String writerAlias;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private String startingDate;
    private String region;
    private String activityCategory;
    private boolean onRecruitment;
}
