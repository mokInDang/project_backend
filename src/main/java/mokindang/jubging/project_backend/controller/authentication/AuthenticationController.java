package mokindang.jubging.project_backend.controller.authentication;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mokindang.jubging.project_backend.domain.member.LoginState;
import mokindang.jubging.project_backend.service.authentication.AuthenticationService;
import mokindang.jubging.project_backend.service.member.request.AuthorizationCodeRequest;
import mokindang.jubging.project_backend.service.member.request.RefreshTokenRequest;
import mokindang.jubging.project_backend.service.member.response.JwtResponse;
import mokindang.jubging.project_backend.service.member.response.KakaoLoginResponse;
import mokindang.jubging.project_backend.service.member.response.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final AuthenticationService authenticationService;

    @PostMapping("/api/member/join")
    public ResponseEntity<LoginResponse> kakaoCallback(@RequestBody AuthorizationCodeRequest authorizationCodeRequest) {
        KakaoLoginResponse kakaoLoginResponse = authenticationService.login(authorizationCodeRequest);
        LoginResponse loginResponse = new LoginResponse(kakaoLoginResponse.getAlias());

        if (kakaoLoginResponse.getLoginState() == LoginState.JOIN) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .header(AUTHORIZATION_HEADER, kakaoLoginResponse.getAccessToken(), kakaoLoginResponse.getRefreshToken())
                    .body(loginResponse);
        }
        return ResponseEntity.ok()
                .header(AUTHORIZATION_HEADER, kakaoLoginResponse.getAccessToken(), kakaoLoginResponse.getRefreshToken())
                .body(loginResponse);
    }

    @PostMapping("/api/member/reissueToken")
    public ResponseEntity<JwtResponse> reissueJwtToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        JwtResponse jwtResponse = authenticationService.reissue(refreshTokenRequest);
        return ResponseEntity.ok()
                .header(AUTHORIZATION_HEADER, jwtResponse.getAccessToken(), jwtResponse.getRefreshToken())
                .build();
    }
}
