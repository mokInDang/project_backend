package mokindang.jubging.project_backend.image.service.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileImageDeleteRequest {

    @Schema(description = "삭제할 이미지 Url")
    private String profileImageUrl;
}
