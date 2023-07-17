package mokindang.jubging.project_backend.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mokindang.jubging.project_backend.exception.ErrorResponse;
import mokindang.jubging.project_backend.member.service.request.MyPageEditRequest;
import mokindang.jubging.project_backend.member.service.request.RegionUpdateRequest;
import mokindang.jubging.project_backend.member.service.response.MyPageResponse;
import mokindang.jubging.project_backend.member.service.response.RegionUpdateResponse;
import mokindang.jubging.project_backend.web.argumentresolver.Login;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Tag(name = "회원", description = "회원 관련 api")
public interface MemberControllerSwagger {

    @Operation(summary = "회원의 지역 변경", parameters = {
            @Parameter(name = AUTHORIZATION, description = "access token", in = ParameterIn.HEADER, required = true)}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "변경완료"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 유저 \t\n" +
                    "입력 받은 위도 경도가 대한민국이 아닌 경우",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PatchMapping("/region")
    ResponseEntity<RegionUpdateResponse> updateRegion(@Parameter(hidden = true) @Login Long memberId, @Valid @RequestBody RegionUpdateRequest regionUpdateRequest);

    @Operation(summary = "회원의 마이페이지 조회", parameters = {
            @Parameter(name = AUTHORIZATION, description = "access token", in = ParameterIn.HEADER, required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회완료")
    })
    @GetMapping("/mypage")
    ResponseEntity<MyPageResponse> callMyPage(@Parameter(hidden = true) @Login Long memberId);

    @Operation(summary = "회원의 내 정보 수정", parameters = {
            @Parameter(name = AUTHORIZATION, description = "access token", in = ParameterIn.HEADER, required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정완료")
    })
    @PatchMapping(value = "/edit-mypage")
    ResponseEntity<MyPageResponse> editMyPage(@Parameter(hidden = true) @Login Long memberId, @RequestBody MyPageEditRequest myPageEditRequest);

}
