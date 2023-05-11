package mokindang.jubging.project_backend.image.service.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@RequiredArgsConstructor
public class ImageRequest {

    @Schema(description = "첨부 이미지", example = "예시 이미지")
    private final MultipartFile image;
}
