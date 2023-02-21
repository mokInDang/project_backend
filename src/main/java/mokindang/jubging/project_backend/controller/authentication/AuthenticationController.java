package mokindang.jubging.project_backend.controller.authentication;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mokindang.jubging.project_backend.domain.member.LoginState;
import mokindang.jubging.project_backend.service.member.MemberService;
import mokindang.jubging.project_backend.service.member.request.RefreshTokenRequest;
import mokindang.jubging.project_backend.service.member.response.JwtResponse;
import mokindang.jubging.project_backend.service.member.response.LoginResponseDto;
import mokindang.jubging.project_backend.service.member.response.LoginStateResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final MemberService memberService;

    @GetMapping("/api/member/join")
    public ResponseEntity<LoginResponseDto> kakaoCallback(@RequestParam String code) {
        LoginStateResponse loginStateResponse = memberService.login(code);
        LoginResponseDto loginResponseDto =
                new LoginResponseDto(loginStateResponse.getAccessToken(), loginStateResponse.getRefreshToken(), loginStateResponse.getAlias());

        if (loginStateResponse.getLoginState() == LoginState.JOIN) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .header("Authorization", loginStateResponse.getAccessToken(), loginStateResponse.getRefreshToken())
                    .body(loginResponseDto);
        }
        return ResponseEntity.ok()
                .header("Authorization", loginStateResponse.getAccessToken(), loginStateResponse.getRefreshToken())
                .body(loginResponseDto);
    }

    @PostMapping("/api/member/reissueToken")
    public ResponseEntity<JwtResponse> reissueJwtToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        JwtResponse jwtResponse = memberService.reissue(refreshTokenRequest);
        return ResponseEntity.ok()
                .header("Authorization", jwtResponse.getAccessToken(), jwtResponse.getRefreshToken())
                .build();
    }
}
