package mokindang.jubging.project_backend.service.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FileResponse {

    @Schema(description = "업로드 이미지 Url", example = "https://testimage.png")
    private final String uploadFileUrl;

    @Schema(description = "업로드 이미지 Name", example = "testimage.png")
    private final String uploadFileName;
}