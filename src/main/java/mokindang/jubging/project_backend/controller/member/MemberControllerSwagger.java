package mokindang.jubging.project_backend.controller.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mokindang.jubging.project_backend.exception.ErrorResponse;
import mokindang.jubging.project_backend.service.file.FileResponse;
import mokindang.jubging.project_backend.service.member.request.RegionUpdateRequest;
import mokindang.jubging.project_backend.service.member.response.MyPageResponse;
import mokindang.jubging.project_backend.service.member.response.RegionUpdateResponse;
import mokindang.jubging.project_backend.web.argumentresolver.Login;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

@Tag(name = "회원", description = "회원 관련 api")
public interface MemberControllerSwagger {

    @Operation(summary = "회원의 지역 변경", parameters = {
            @Parameter(name = AUTHORIZATION, description = "access token", in = ParameterIn.HEADER, required = true),
            @Parameter(name = SET_COOKIE, description = "refreshToken", in = ParameterIn.COOKIE, required = true)}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "변경완료"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 유저 \t\n"+
                    "입력 받은 위도 경도가 대한민국이 아닌 경우",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    ResponseEntity<RegionUpdateResponse> updateRegion(@Parameter(hidden = true) @Login Long memberId, @Valid @RequestBody RegionUpdateRequest regionUpdateRequest);

    @Operation(summary = "회원의 마이페이지 조회", parameters = {
            @Parameter(name = AUTHORIZATION, description = "access token", in = ParameterIn.HEADER, required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회완료")
    })
    ResponseEntity<MyPageResponse> callMyPage(@Parameter(hidden = true) @Login Long memberId);

    @Operation(summary = "회원 프로필 이미지 수정", parameters = {
            @Parameter(name = AUTHORIZATION, description = "access token", in = ParameterIn.HEADER, required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업로드 완료"),
            @ApiResponse(responseCode = "400", description = "이미지가 선택되지 않았습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<FileResponse> uploadProfileImage(@Parameter(hidden = true) @Login Long memberId, @RequestPart(value = "file") MultipartFile multipartFile);

}
