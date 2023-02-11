package mokindang.jubging.project_backend.controller.authentication;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mokindang.jubging.project_backend.domain.member.LoginResponseDto;
import mokindang.jubging.project_backend.service.member.MemberService;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final MemberService memberService;

    @GetMapping("api/member/join")
    public LoginResponseDto kakaoCallback(@RequestParam String code) {
        return memberService.login(code);
    }
}
