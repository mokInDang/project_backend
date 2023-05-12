package mokindang.jubging.project_backend.image.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ImageUrlResponse {

    @Schema(description = "반환될 이미지 url", example = "https://image.png")
    private String imageUrl;
}
