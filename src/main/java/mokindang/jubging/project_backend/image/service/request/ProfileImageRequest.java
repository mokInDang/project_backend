package mokindang.jubging.project_backend.image.service.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileImageRequest {

    private MultipartFile profileImage;
}
