package mokindang.jubging.project_backend.service.board.request;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class BoardCreateRequest {

    @NotNull
    private final String title;

    @NotNull
    private final String content;

    @NotNull
    private final String activityCategory;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private final LocalDate startDate;
}
