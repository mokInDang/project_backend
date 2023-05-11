package mokindang.jubging.project_backend.image.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mokindang.jubging.project_backend.image.service.request.ImageDeleteRequest;
import mokindang.jubging.project_backend.image.service.request.ImageRequest;
import mokindang.jubging.project_backend.image.service.response.ImageUrlResponse;
import mokindang.jubging.project_backend.web.argumentresolver.Login;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Tag(name = "이미지", description = "이미지 관련 api")
public interface ImageControllerSwagger {

    @Operation(summary = "프로필 이미지 추가", parameters = {
            @Parameter(name = AUTHORIZATION, description = "access token", in = ParameterIn.HEADER, required = true)}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "변경완료")
    })
    @PostMapping("profile")
    ResponseEntity<ImageUrlResponse> uploadProfileImage(@Parameter(hidden = true) @Login Long memberId, @ModelAttribute ImageRequest imageRequest);

    @Operation(summary = "프로필 이미지 삭제", parameters = {
            @Parameter(name = AUTHORIZATION, description = "access token", in = ParameterIn.HEADER, required = true)}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제완료")
    })
    @DeleteMapping("profile")
    ResponseEntity<ImageUrlResponse> deleteProfileImage(@Parameter(hidden = true) @RequestBody ImageDeleteRequest imageDeleteRequest);

    @Operation(summary = "인증게시글 이미지 추가", parameters = {
            @Parameter(name = AUTHORIZATION, description = "access token", in = ParameterIn.HEADER, required = true)}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "변경완료")
    })
    @PostMapping("certification")
    ResponseEntity<ImageUrlResponse> uploadCertificationImage(@Parameter(hidden = true) @Login Long memberId, @ModelAttribute ImageRequest imageRequest);

    @Operation(summary = "인증게시글 이미지 삭제", parameters = {
            @Parameter(name = AUTHORIZATION, description = "access token", in = ParameterIn.HEADER, required = true)}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제완료")
    })
    @DeleteMapping("certification")
    ResponseEntity<Void> deleteCertificationImage(@Parameter(hidden = true) @RequestBody ImageDeleteRequest imageDeleteRequest);

}
