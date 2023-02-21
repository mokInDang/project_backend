package mokindang.jubging.project_backend.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ErrorResponse {

    @Schema(name = "에러 메세지")
    private final String error;
}
