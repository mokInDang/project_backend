package mokindang.jubging.project_backend.service.file;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileResponse {

    private String uploadFileUrl;

    private String uploadFileName;
}