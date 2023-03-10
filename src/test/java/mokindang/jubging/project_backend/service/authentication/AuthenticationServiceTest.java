package mokindang.jubging.project_backend.service.authentication;

import io.jsonwebtoken.JwtException;
import mokindang.jubging.project_backend.domain.member.LoginState;
import mokindang.jubging.project_backend.domain.member.Member;
import mokindang.jubging.project_backend.domain.token.RefreshToken;
import mokindang.jubging.project_backend.repository.token.RefreshTokenRepository;
import mokindang.jubging.project_backend.service.member.MemberService;
import mokindang.jubging.project_backend.service.member.request.AuthorizationCodeRequest;
import mokindang.jubging.project_backend.service.member.response.JwtResponse;
import mokindang.jubging.project_backend.service.member.response.KakaoApiMemberResponse;
import mokindang.jubging.project_backend.service.member.response.KakaoLoginResponse;
import mokindang.jubging.project_backend.web.jwt.TokenManager;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
class AuthenticationServiceTest {

    @Mock
    private MemberService memberService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private KaKaoOAuth2 kakaoOAuth2;

    @Mock
    private TokenManager tokenManager;

    @Mock
    private RefreshToken refreshToken;

    @InjectMocks
    private AuthenticationService authenticationService;

    private final SoftAssertions softly = new SoftAssertions();

    @Test
    @DisplayName("DB??? email??? ?????? ??? ???????????? flow??? ????????????. access token, refresh token, email ??? 4??????, alias, region, ????????? ?????? JOIN ??? ????????????.")
    void authenticateJoin() {
        //given
        KakaoApiMemberResponse kakaoApiMemberResponse = new KakaoApiMemberResponse("dog123@test.com", "minho");

        when(kakaoOAuth2.getMemberDto(anyString())).thenReturn(kakaoApiMemberResponse);
        when(memberService.findByMemberEmail(any())).thenReturn(Optional.empty());
        when(memberService.saveMember(any())).thenReturn(new Member("dog123@test.com", "minho"));
        when(tokenManager.createToken(any())).thenReturn("Test Access Token");

        //when
        KakaoLoginResponse kakaoLoginResponse = authenticationService.login(new AuthorizationCodeRequest("Test Authorization Code"));

        //then
        softly.assertThat(kakaoLoginResponse.getAccessToken()).isEqualTo("Test Access Token");
        softly.assertThat(kakaoLoginResponse.getRefreshToken()).isNotEmpty();
        softly.assertThat(kakaoLoginResponse.getEmail()).isEqualTo("dog1");
        softly.assertThat(kakaoLoginResponse.getAlias()).isEqualTo("minho");
        softly.assertThat(kakaoLoginResponse.getRegion()).isEqualTo("DEFAULT_REGION");
        softly.assertThat(kakaoLoginResponse.getLoginState()).isEqualTo(LoginState.JOIN);
        softly.assertAll();
        verify(refreshTokenRepository, times(1)).save(any());

    }

    @Test
    @DisplayName("DB??? email ????????? ??? ????????? flow??? ????????????. access token, refresh token, email ??? 4??????, alias, region, ????????? ?????? LOGIN ??? ????????????.")
    void authenticateLogin() {
        //given
        Member existMember = new Member("dog123@test.com", "minho");
        existMember.updateRegion("?????????");
        KakaoApiMemberResponse kakaoApiMemberResponse = new KakaoApiMemberResponse("dog123@test.com", "minho");


        when(kakaoOAuth2.getMemberDto(anyString())).thenReturn(kakaoApiMemberResponse);
        when(memberService.findByMemberEmail(any())).thenReturn(Optional.of(existMember));
        when(tokenManager.createToken(any())).thenReturn("Test Access Token");

        //when
        KakaoLoginResponse kakaoLoginResponse = authenticationService.login(new AuthorizationCodeRequest("Test Authorization Code"));

        //then
        softly.assertThat(kakaoLoginResponse.getAccessToken()).isEqualTo("Test Access Token");
        softly.assertThat(kakaoLoginResponse.getRefreshToken()).isNotEmpty();
        softly.assertThat(kakaoLoginResponse.getEmail()).isEqualTo("dog1");
        softly.assertThat(kakaoLoginResponse.getAlias()).isEqualTo("minho");
        softly.assertThat(kakaoLoginResponse.getRegion()).isEqualTo("?????????");
        softly.assertThat(kakaoLoginResponse.getLoginState()).isEqualTo(LoginState.LOGIN);
        softly.assertAll();
        verify(refreshTokenRepository, times(1)).save(any());

    }

    @Test
    @DisplayName("RefreshToken??? ?????? ?????? ???????????? ??????, ????????? ?????? Access Token, Refresh Token ???????????????.")
    void reissue() {
        //given
        Member member = new Member("koho1047@naver.com", "?????????");

        when(refreshTokenRepository.findByToken(any())).thenReturn(Optional.of(refreshToken));
        when(memberService.findByMemberId(any())).thenReturn(member);
        when(tokenManager.createToken(any())).thenReturn("Test Access Token");

        //when
        JwtResponse reissue = authenticationService.reissue("Test Refresh Token");

        //then
        softly.assertThat(reissue.getAccessToken()).isEqualTo("Test Access Token");
        softly.assertThat(reissue.getRefreshToken()).isNotEqualTo("Test Refresh Token");
        softly.assertAll();
        verify(refreshToken, times(1)).switchRefreshToken(any(), any());
    }

    @Test
    @DisplayName("RefreshToken??? ?????? ?????? ???????????? ??????, ???????????? ?????? ?????? ????????? ????????????.")
    void reissueError() {
        //given
        when(refreshTokenRepository.findByToken(anyString())).thenThrow(new JwtException("Refresh Token ??? ???????????? ????????????."));

        //when, then
        Assertions.assertThatThrownBy(() -> authenticationService.reissue("Test Refresh Token")).isInstanceOf(JwtException.class)
                .hasMessage("Refresh Token ??? ???????????? ????????????.");
    }
}
