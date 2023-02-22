package mokindang.jubging.project_backend.service.authentication;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mokindang.jubging.project_backend.domain.member.Member;
import mokindang.jubging.project_backend.domain.token.RefreshToken;
import mokindang.jubging.project_backend.repository.token.RefreshTokenRepository;
import mokindang.jubging.project_backend.security.kakao.KaKaoOAuth2;
import mokindang.jubging.project_backend.service.member.MemberService;
import mokindang.jubging.project_backend.service.member.request.RefreshTokenRequest;
import mokindang.jubging.project_backend.service.member.response.JwtResponse;
import mokindang.jubging.project_backend.service.member.response.KakaoApiMemberResponse;
import mokindang.jubging.project_backend.service.member.response.LoginStateResponse;
import mokindang.jubging.project_backend.web.jwt.TokenManager;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static mokindang.jubging.project_backend.domain.member.LoginState.JOIN;
import static mokindang.jubging.project_backend.domain.member.LoginState.LOGIN;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final MemberService memberService;
    private final KaKaoOAuth2 kakaoOAuth2;
    private final TokenManager tokenManager;
    private final RefreshTokenRepository refreshTokenRepository;


    public LoginStateResponse login(final String authorizedCode) {
        KakaoApiMemberResponse kakaoApiMemberResponse = kakaoOAuth2.getMemberDto(authorizedCode);
        return authenticate(kakaoApiMemberResponse);
    }

    private LoginStateResponse authenticate(final KakaoApiMemberResponse kakaoApiMemberResponse) {
        Optional<Member> member = memberService.findByMemberEmail(kakaoApiMemberResponse.getEmail());
        if (member.isEmpty()) {
            return join(kakaoApiMemberResponse);
        }
        log.info("memberId = {}, email = {}, alias = {} 의 로그인", member.get().getId(), kakaoApiMemberResponse.getEmail(), kakaoApiMemberResponse.getAlias());
        JwtResponse jwtResponse = issueJwtToken(member.get());
        return new LoginStateResponse(jwtResponse.getAccessToken(),
                jwtResponse.getRefreshToken(), member.orElseThrow(IllegalArgumentException::new).getAlias(), LOGIN);
    }

    private LoginStateResponse join(final KakaoApiMemberResponse kakaoApiMemberResponse) {
        log.info("email = {}, alias = {} 의 회원 가입", kakaoApiMemberResponse.getEmail(), kakaoApiMemberResponse.getAlias());
        Member newMember = memberService.saveMember(new Member(kakaoApiMemberResponse.getEmail(), kakaoApiMemberResponse.getAlias()));
        JwtResponse jwtResponse = issueJwtToken(newMember);
        return new LoginStateResponse(jwtResponse.getAccessToken(), jwtResponse.getRefreshToken(), newMember.getAlias(), JOIN);
    }

    private JwtResponse issueJwtToken(final Member member) {
        String newRefreshToken = UUID.randomUUID().toString();
        String newAccessToken = tokenManager.createToken(member.getId());
        issue(member, newRefreshToken, newAccessToken);
        return new JwtResponse(newAccessToken, newRefreshToken);
    }

    private void issue(final Member member, final String newRefreshToken, final String newAccessToken) {
        refreshTokenRepository.findByMemberId(member.getId())
                .ifPresentOrElse(
                        existRefreshToken -> {
                            existRefreshToken.switchRefreshToken(newRefreshToken, LocalDateTime.now());
                            log.info("JWT 토큰 재발행 - MemberId : {}, Email : {}, Alias : {}, Access Token : {}, RefreshToken : {} ",
                                    member.getId(), member.getEmail(), member.getAlias(), newAccessToken, newRefreshToken);
                        },
                        () -> {
                            LocalDateTime newTokenExpirationTime = LocalDateTime.now()
                                    .plusMonths(2);

                            RefreshToken refreshToken = new RefreshToken(member.getId(), newRefreshToken, newTokenExpirationTime);
                            refreshTokenRepository.save(refreshToken);
                            log.info("JWT 토큰 발행 - MemberId : {}, Email : {}, Alias : {}, Access Token : {}, RefreshToken : {} ",
                                    member.getId(), member.getEmail(), member.getAlias(), newAccessToken, newRefreshToken);
                        }
                );
    }

    public JwtResponse reissue(final RefreshTokenRequest request) {
        RefreshToken existRefreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new JwtException("Refresh Token 이 존재하지 않습니다."));
        String newRefreshToken = UUID.randomUUID().toString();
        existRefreshToken.switchRefreshToken(newRefreshToken, LocalDateTime.now());
        Member member = memberService.findByMemberId(existRefreshToken.getId());
        return new JwtResponse(tokenManager.createToken(member.getId()), newRefreshToken);
    }
}
