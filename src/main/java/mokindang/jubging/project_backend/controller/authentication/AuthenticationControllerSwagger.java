package mokindang.jubging.project_backend.controller.authentication;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mokindang.jubging.project_backend.exception.ErrorResponse;
import mokindang.jubging.project_backend.service.member.request.AuthorizationCodeRequest;
import mokindang.jubging.project_backend.service.member.response.JwtResponse;
import mokindang.jubging.project_backend.service.member.response.LoginResponse;
import mokindang.jubging.project_backend.web.argumentresolver.Login;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

@Tag(name = "인증", description = "인증 관련 api")
public interface AuthenticationControllerSwagger {

    @Operation(summary = "카카오 로그인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카카오 로그안 완료", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
    })
    ResponseEntity<LoginResponse> kakaoLogin(@RequestBody AuthorizationCodeRequest authorizationCodeRequest);

    @Operation(summary = "Refresh 토큰 재요청", parameters = {
            @Parameter(name = AUTHORIZATION, description = "access token", in = ParameterIn.HEADER, required = true),
            @Parameter(name = SET_COOKIE, description = "refreshToken", in = ParameterIn.COOKIE, required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Refresh 토큰 재발급 완료"),
            @ApiResponse(responseCode = "401", description = "존재 하지 않는 Refresh 토큰 입력\t\n" +
                    "Refresh 토큰이 만료됨", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "refresh token 이 null 이나 공백으로 입력 \t\n" +
                    "생성된 토큰이 기존 토큰과 같음\t\n", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<JwtResponse> reissueJwtToken(@CookieValue(name = "refreshToken") String refreshToken);

    @Operation(summary = "회원 로그아웃 요청", parameters = {
            @Parameter(name = AUTHORIZATION, description = "access token", in = ParameterIn.HEADER, required = true),
            @Parameter(name = SET_COOKIE, description = "refreshToken", in = ParameterIn.COOKIE, required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "회원 로그아웃 완료")
    })
    ResponseEntity<Void> logout(@Login Long memberId);
}
