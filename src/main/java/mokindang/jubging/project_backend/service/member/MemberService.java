package mokindang.jubging.project_backend.service.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mokindang.jubging.project_backend.domain.member.Member;
import mokindang.jubging.project_backend.repository.member.MemberRepository;
import mokindang.jubging.project_backend.security.kakao.KaKaoOAuth2;
import mokindang.jubging.project_backend.service.member.dto.KakaoApiMemberResponse;
import mokindang.jubging.project_backend.service.member.dto.LoginStateDto;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static mokindang.jubging.project_backend.domain.member.MemberState.JOIN;
import static mokindang.jubging.project_backend.domain.member.MemberState.LOGIN;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final KaKaoOAuth2 kakaoOAuth2;

    public LoginStateDto login(String authorizedCode) {
        KakaoApiMemberResponse memberDto = kakaoOAuth2.getMemberDto(authorizedCode);
        return authenticate(memberDto);
    }

    private LoginStateDto authenticate(KakaoApiMemberResponse kakaoApiMemberResponse) {
        Optional<Member> member = memberRepository.findByEmail(kakaoApiMemberResponse.getEmail());
        if (member.isEmpty()) {
            return join(kakaoApiMemberResponse);
        }
        log.info("email = {}, alias = {} 의 로그인", kakaoApiMemberResponse.getEmail(), kakaoApiMemberResponse.getAlias());
        return new LoginStateDto(kakaoApiMemberResponse.getAccessToken(),
                kakaoApiMemberResponse.getRefreshToken(), member.orElseThrow(IllegalArgumentException::new).getAlias(), LOGIN);
    }

    private LoginStateDto join(final KakaoApiMemberResponse kakaoApiMemberResponse) {
        log.info("email = {}, alias = {} 의 회원 가입", kakaoApiMemberResponse.getEmail(), kakaoApiMemberResponse.getAlias());
        Member newMember = memberRepository.save(new Member(kakaoApiMemberResponse.getEmail(), kakaoApiMemberResponse.getAlias()));
        return new LoginStateDto(kakaoApiMemberResponse.getAccessToken(), kakaoApiMemberResponse.getRefreshToken(), newMember.getAlias(), JOIN);
    }

    public Member findByMemberId(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 유저가 존재하지 않습니다."));
    }
}
