package mokindang.jubging.project_backend.authentication.service;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mokindang.jubging.project_backend.member.domain.Member;
import mokindang.jubging.project_backend.authentication.domain.token.RefreshToken;
import mokindang.jubging.project_backend.authentication.repository.RefreshTokenRepository;
import mokindang.jubging.project_backend.member.service.MemberService;
import mokindang.jubging.project_backend.member.service.request.AuthorizationCodeRequest;
import mokindang.jubging.project_backend.member.service.response.JwtResponse;
import mokindang.jubging.project_backend.member.service.response.KakaoApiMemberResponse;
import mokindang.jubging.project_backend.member.service.response.KakaoLoginResponse;
import mokindang.jubging.project_backend.web.jwt.TokenManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static mokindang.jubging.project_backend.member.domain.LoginState.JOIN;
import static mokindang.jubging.project_backend.member.domain.LoginState.LOGIN;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthenticationService {

    private final MemberService memberService;
    private final KaKaoOAuth2 kakaoOAuth2;
    private final TokenManager tokenManager;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public KakaoLoginResponse login(final AuthorizationCodeRequest authorizationCodeRequest) {
        KakaoApiMemberResponse kakaoApiMemberResponse = kakaoOAuth2.getMemberDto(authorizationCodeRequest.getAuthorizationCode());
        return authenticate(kakaoApiMemberResponse);
    }

    private KakaoLoginResponse authenticate(final KakaoApiMemberResponse kakaoApiMemberResponse) {
        Optional<Member> member = memberService.findByMemberEmail(kakaoApiMemberResponse.getEmail());
        if (member.isEmpty()) {
            return join(kakaoApiMemberResponse);
        }
        Member existMember = member.get();
        log.info("memberId = {}, email = {}, alias = {} 의 로그인", existMember.getId(), kakaoApiMemberResponse.getEmail(), kakaoApiMemberResponse.getAlias());
        JwtResponse jwtResponse = issueJwtToken(existMember);
        return new KakaoLoginResponse(jwtResponse.getAccessToken(),
                jwtResponse.getRefreshToken(), existMember.getFirstFourDigitsOfWriterEmail(), existMember.getAlias(), existMember.getRegion().getValue(), existMember.getProfileImage().getProfileImageUrl(), LOGIN);
    }

    private KakaoLoginResponse join(final KakaoApiMemberResponse kakaoApiMemberResponse) {
        log.info("email = {}, alias = {} 의 회원 가입", kakaoApiMemberResponse.getEmail(), kakaoApiMemberResponse.getAlias());
        Member newMember = memberService.saveMember(new Member(kakaoApiMemberResponse.getEmail(), kakaoApiMemberResponse.getAlias()));
        JwtResponse jwtResponse = issueJwtToken(newMember);
        return new KakaoLoginResponse(jwtResponse.getAccessToken(),
                jwtResponse.getRefreshToken(), newMember.getFirstFourDigitsOfWriterEmail(), newMember.getAlias(), newMember.getRegion().getValue(), newMember.getProfileImage().getProfileImageUrl(), JOIN);
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
                                    .plusDays(60);

                            RefreshToken refreshToken = new RefreshToken(member.getId(), newRefreshToken, newTokenExpirationTime);
                            refreshTokenRepository.save(refreshToken);
                            log.info("JWT 토큰 발행 - MemberId : {}, Email : {}, Alias : {}, Access Token : {}, RefreshToken : {} ",
                                    member.getId(), member.getEmail(), member.getAlias(), newAccessToken, newRefreshToken);
                        }
                );
    }

    @Transactional
    public JwtResponse reissue(final String refreshToken) {
        RefreshToken existRefreshToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new JwtException("Refresh Token 이 존재하지 않습니다."));
        String newRefreshToken = UUID.randomUUID().toString();
        existRefreshToken.switchRefreshToken(newRefreshToken, LocalDateTime.now());
        Member member = memberService.findByMemberId(existRefreshToken.getMemberId());
        return new JwtResponse(tokenManager.createToken(member.getId()), newRefreshToken);
    }

    @Transactional
    public void logout(final Long memberId) {
        log.info("memberId = {} 의 로그아웃 요청", memberId);
        refreshTokenRepository.deleteAllByMemberId(memberId);
    }
}
