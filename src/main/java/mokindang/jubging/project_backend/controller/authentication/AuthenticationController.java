package mokindang.jubging.project_backend.controller.authentication;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mokindang.jubging.project_backend.domain.member.LoginState;
import mokindang.jubging.project_backend.service.authentication.AuthenticationService;
import mokindang.jubging.project_backend.service.member.request.AuthorizationCodeRequest;
import mokindang.jubging.project_backend.service.member.response.JwtResponse;
import mokindang.jubging.project_backend.service.member.response.KakaoLoginResponse;
import mokindang.jubging.project_backend.service.member.response.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String SET_COOKIE = "Set-Cookie";

    private final AuthenticationService authenticationService;


    @PostMapping("/api/member/join")
    public ResponseEntity<LoginResponse> kakaoCallback(@RequestBody AuthorizationCodeRequest authorizationCodeRequest) {
        KakaoLoginResponse kakaoLoginResponse = authenticationService.login(authorizationCodeRequest);
        LoginResponse loginResponse = new LoginResponse(kakaoLoginResponse.getAlias());
        ResponseCookie responseCookie = createCookie(kakaoLoginResponse.getRefreshToken());

        if (kakaoLoginResponse.getLoginState() == LoginState.JOIN) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .header(AUTHORIZATION_HEADER, kakaoLoginResponse.getAccessToken())
                    .header(SET_COOKIE, responseCookie.toString())
                    .body(loginResponse);
        }
        return ResponseEntity.ok()
                .header(AUTHORIZATION_HEADER, kakaoLoginResponse.getAccessToken())
                .header(SET_COOKIE, responseCookie.toString())
                .body(loginResponse);
    }

    @PostMapping("/api/member/reissueToken")
    public ResponseEntity<JwtResponse> reissueJwtToken(@RequestHeader(SET_COOKIE) String refreshToken) {
        JwtResponse jwtResponse = authenticationService.reissue(refreshToken);
        ResponseCookie responseCookie = createCookie(jwtResponse.getRefreshToken());

        return ResponseEntity.ok()
                .header(AUTHORIZATION_HEADER, jwtResponse.getAccessToken())
                .header(SET_COOKIE, responseCookie.toString())
                .build();
    }

    private static ResponseCookie createCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .build();
    }
}
