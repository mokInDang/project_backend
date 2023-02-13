package mokindang.jubging.project_backend.service.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mokindang.jubging.project_backend.domain.member.Member;
import mokindang.jubging.project_backend.service.member.dto.MemberStateDto;
import mokindang.jubging.project_backend.service.member.dto.KakaoApiMemberResponse;
import mokindang.jubging.project_backend.repository.member.MemberRepository;
import mokindang.jubging.project_backend.security.kakao.KaKaoOAuth2;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static mokindang.jubging.project_backend.domain.member.MemberState.JOIN;
import static mokindang.jubging.project_backend.domain.member.MemberState.LOGIN;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final KaKaoOAuth2 kakaoOAuth2;

    public MemberStateDto login(String authorizedCode) {
        KakaoApiMemberResponse memberDto = kakaoOAuth2.getMemberDto(authorizedCode);
        return authenticate(memberDto);
    }
    private MemberStateDto authenticate(KakaoApiMemberResponse memberDto) {
        Optional<Member> member = memberRepository.findByEmail(memberDto.getEmail());
        if (member.isEmpty()) {
            Member newMember = memberRepository.save(new Member(memberDto.getEmail(), memberDto.getAlias()));
            return new MemberStateDto(memberDto.getAccessToken(), memberDto.getRefreshToken(), newMember.getAlias(), JOIN);
        }
        return new MemberStateDto(memberDto.getAccessToken(),
                memberDto.getRefreshToken(), member.orElseThrow(IllegalArgumentException::new).getAlias(),LOGIN);
    }
    public Member findByMemberId(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 유저가 존재하지 않습니다."));
    }
    
}
