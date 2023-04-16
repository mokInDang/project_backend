package mokindang.jubging.project_backend.service.board.certificationboard.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class CertificationBoardCreationRequest {

    @NotNull
    @NotBlank
    private final String title;

    @NotNull
    @NotBlank
    private final String content;

    private final List<MultipartFile> files;
}
