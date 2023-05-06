package mokindang.jubging.project_backend.image.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mokindang.jubging.project_backend.image.service.ImageService;
import mokindang.jubging.project_backend.image.service.request.ProfileImageDeleteRequest;
import mokindang.jubging.project_backend.image.service.request.ProfileImageRequest;
import mokindang.jubging.project_backend.image.service.response.ProfileImageResponse;
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

    @PostMapping(value = "profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProfileImageResponse> uploadProfileImage(@Login long memberId, @ModelAttribute ProfileImageRequest profileImageRequest) {
        ProfileImageResponse profileImageResponse = imageService.uploadProfileImage(memberId, profileImageRequest);
        return ResponseEntity.ok()
                .body(profileImageResponse);
    }

    @DeleteMapping("profile-image")
    public ResponseEntity<ProfileImageResponse> deleteProfileImage(@RequestBody ProfileImageDeleteRequest profileImageDeleteRequest) {
        ProfileImageResponse profileImageResponse = imageService.deleteProfileImage(profileImageDeleteRequest);
        return ResponseEntity.ok()
                .body(profileImageResponse);
    }
}
