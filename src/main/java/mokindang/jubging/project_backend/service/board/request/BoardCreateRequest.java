package mokindang.jubging.project_backend.service.board.request;

import com.sun.istack.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class BoardCreateRequest {

    @NotNull
    private final String title;

    @NotNull
    private final String content;

    @NotNull
    private final String activityCategory;

    @NotNull
    private final LocalDate startDate;

    public BoardCreateRequest(final String title, final String content, final String activityCategory, final LocalDate startDate) {
        this.title = title;
        this.content = content;
        this.activityCategory = activityCategory;
        this.startDate = startDate;
    }
}
