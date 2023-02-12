package mokindang.jubging.project_backend.controller.authentication;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mokindang.jubging.project_backend.service.member.dto.LoginResponseDto;
import mokindang.jubging.project_backend.domain.member.MemberState;
import mokindang.jubging.project_backend.service.member.dto.MemberStateDto;
import mokindang.jubging.project_backend.service.member.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final MemberService memberService;

    @GetMapping("api/member/join")
    public ResponseEntity<LoginResponseDto> kakaoCallback(@RequestParam String code) {
        MemberStateDto memberStateDto = memberService.login(code);
        LoginResponseDto loginResponseDto = new LoginResponseDto(memberStateDto.getAccessToken(),
                                                memberStateDto.getRefreshToken(), memberStateDto.getAlias());

        if (memberStateDto.getMemberState() == MemberState.JOIN) {
            return new ResponseEntity<>(loginResponseDto, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(loginResponseDto,HttpStatus.OK);
    }
}
