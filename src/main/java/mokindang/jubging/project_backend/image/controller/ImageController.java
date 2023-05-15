package mokindang.jubging.project_backend.image.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mokindang.jubging.project_backend.image.service.ImageService;
import mokindang.jubging.project_backend.image.service.request.ImageRequest;
import mokindang.jubging.project_backend.image.service.response.ImageUrlResponse;
import mokindang.jubging.project_backend.web.argumentresolver.Login;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class ImageController implements ImageControllerSwagger{

    private final ImageService imageService;

    @PostMapping(value = "/member/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageUrlResponse> uploadProfileImage(@Login Long memberId, @ModelAttribute ImageRequest imageRequest) {
        ImageUrlResponse imageUrlResponse = imageService.uploadProfileImage(memberId, imageRequest);
        return ResponseEntity.ok()
                .body(imageUrlResponse);
    }

    @DeleteMapping("/member/profile-image")
    public ResponseEntity<ImageUrlResponse> deleteProfileImage() {
        ImageUrlResponse defaultImageUrl = imageService.getDefaultImageUrl();
        return ResponseEntity.ok()
                .body(defaultImageUrl);
    }

    @PostMapping(value = "/certification-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageUrlResponse> uploadCertificationImage(@Login Long memberId, @ModelAttribute ImageRequest imageRequest) {
        ImageUrlResponse imageUrlResponse = imageService.uploadCertificationImage(memberId, imageRequest);
        return ResponseEntity.ok()
                .body(imageUrlResponse);
    }

    @DeleteMapping("/certification-image")
    public ResponseEntity<Void> deleteCertificationImage() {
        return ResponseEntity.ok()
                .build();
    }
}
