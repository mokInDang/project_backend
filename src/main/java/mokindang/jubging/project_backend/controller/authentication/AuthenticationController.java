package mokindang.jubging.project_backend.controller.authentication;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mokindang.jubging.project_backend.domain.member.MemberState;
import mokindang.jubging.project_backend.service.member.MemberService;
import mokindang.jubging.project_backend.service.member.dto.LoginResponseDto;
import mokindang.jubging.project_backend.service.member.dto.LoginStateDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final MemberService memberService;

    @GetMapping("/api/member/join")
    public ResponseEntity<LoginResponseDto> kakaoCallback(@RequestParam String code) {
        LoginStateDto loginStateDto = memberService.login(code);
        LoginResponseDto loginResponseDto =
                new LoginResponseDto(loginStateDto.getAccessToken(), loginStateDto.getRefreshToken(), loginStateDto.getAlias());

        if (loginStateDto.getMemberState() == MemberState.JOIN) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(loginResponseDto);
        }
        return ResponseEntity.ok()
                .body(loginResponseDto);
    }
}
