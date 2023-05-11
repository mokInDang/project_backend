package mokindang.jubging.project_backend.image.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mokindang.jubging.project_backend.image.service.ImageService;
import mokindang.jubging.project_backend.image.service.request.ImageDeleteRequest;
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

    @PostMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageUrlResponse> uploadProfileImage(@Login Long memberId, @ModelAttribute ImageRequest imageRequest) {
        ImageUrlResponse imageUrlResponse = imageService.uploadProfileImage(memberId, imageRequest);
        return ResponseEntity.ok()
                .body(imageUrlResponse);
    }

    @DeleteMapping("/profile")
    public ResponseEntity<ImageUrlResponse> deleteProfileImage(@RequestBody ImageDeleteRequest imageDeleteRequest) {
        ImageUrlResponse imageUrlResponse = imageService.deleteProfileImage(imageDeleteRequest);
        return ResponseEntity.ok()
                .body(imageUrlResponse);
    }

    @PostMapping(value = "/certification", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageUrlResponse> uploadCertificationImage(@Login Long memberId, @ModelAttribute ImageRequest imageRequest) {
        ImageUrlResponse imageUrlResponse = imageService.uploadCertificationImage(memberId, imageRequest);
        return ResponseEntity.ok()
                .body(imageUrlResponse);
    }

    @DeleteMapping("/certification")
    public ResponseEntity<Void> deleteCertificationImage(@RequestBody ImageDeleteRequest imageDeleteRequest) {
        imageService.deleteCertificationImage(imageDeleteRequest);
        return ResponseEntity.ok()
                .build();
    }
}
