package mokindang.jubging.project_backend.authentication.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mokindang.jubging.project_backend.member.domain.LoginState;
import mokindang.jubging.project_backend.authentication.service.AuthenticationService;
import mokindang.jubging.project_backend.member.service.request.AuthorizationCodeRequest;
import mokindang.jubging.project_backend.member.service.response.JwtResponse;
import mokindang.jubging.project_backend.member.service.response.KakaoLoginResponse;
import mokindang.jubging.project_backend.member.service.response.LoginResponse;
import mokindang.jubging.project_backend.web.argumentresolver.Login;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

import static org.springframework.boot.web.server.Cookie.SameSite.NONE;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

@Slf4j
@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
public class AuthenticationController implements AuthenticationControllerSwagger{

    private final AuthenticationService authenticationService;

    @PostMapping("/join")
    public ResponseEntity<LoginResponse> kakaoLogin(@RequestBody AuthorizationCodeRequest authorizationCodeRequest) {
        KakaoLoginResponse kakaoLoginResponse = authenticationService.login(authorizationCodeRequest);
        LoginResponse loginResponse = new LoginResponse(kakaoLoginResponse.getEmail(), kakaoLoginResponse.getAlias(), kakaoLoginResponse.getRegion(), kakaoLoginResponse.getProfileImageUrl());
        HttpCookie httpCookie = createCookie(kakaoLoginResponse.getRefreshToken());

        if (kakaoLoginResponse.getLoginState() == LoginState.JOIN) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .header(AUTHORIZATION, kakaoLoginResponse.getAccessToken())
                    .header(SET_COOKIE, httpCookie.toString())
                    .body(loginResponse);
        }
        return ResponseEntity.ok()
                .header(AUTHORIZATION, kakaoLoginResponse.getAccessToken())
                .header(SET_COOKIE, httpCookie.toString())
                .body(loginResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> reissueJwtToken(@CookieValue(name = "refreshToken") String refreshToken) {
        JwtResponse jwtResponse = authenticationService.reissue(refreshToken);
        HttpCookie httpCookie = createCookie(jwtResponse.getRefreshToken());

        return ResponseEntity.ok()
                .header(AUTHORIZATION, jwtResponse.getAccessToken())
                .header(SET_COOKIE, httpCookie.toString())
                .build();
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(@Login Long memberId) {
        authenticationService.logout(memberId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }

    private ResponseCookie createCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite(NONE.attributeValue())
                .maxAge(Duration.ofDays(60))
                .build();
    }
}
