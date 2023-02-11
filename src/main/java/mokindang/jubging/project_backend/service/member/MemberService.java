package mokindang.jubging.project_backend.service.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mokindang.jubging.project_backend.domain.member.Member;
import mokindang.jubging.project_backend.service.member.dto.KakaoApiMemberResponse;
import mokindang.jubging.project_backend.domain.member.LoginResponseDto;
import mokindang.jubging.project_backend.repository.member.MemberRepository;
import mokindang.jubging.project_backend.security.kakao.KaKaoOAuth2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final KaKaoOAuth2 kakaoOAuth2;

    public LoginResponseDto login(String authorizedCode) {
        KakaoApiMemberResponse memberDto = kakaoOAuth2.getMemberDto(authorizedCode);
        Member member = authenticate(memberDto.getEmail(), memberDto.getAlias());
        return new LoginResponseDto(memberDto.getAccessToken(), memberDto.getRefreshToken(), member.getAlias());
    }

    private Member authenticate(String email, String alias) {
        return memberRepository.findByEmail(email)
                .orElseGet(() -> memberRepository.save(new Member(email, alias)));
    }

    public Member findByMemberId(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 유저가 존재하지 않습니다."));
    }
    
}
