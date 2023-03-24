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
import mokindang.jubging.project_backend.service.member.request.RegionUpdateRequest;
import mokindang.jubging.project_backend.service.member.response.RegionUpdateResponse;
import mokindang.jubging.project_backend.web.argumentresolver.Login;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

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
}
