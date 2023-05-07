package mokindang.jubging.project_backend.service.authentication;

import io.jsonwebtoken.JwtException;
import mokindang.jubging.project_backend.authentication.domain.token.RefreshToken;
import mokindang.jubging.project_backend.authentication.repository.RefreshTokenRepository;
import mokindang.jubging.project_backend.authentication.service.AuthenticationService;
import mokindang.jubging.project_backend.authentication.service.KaKaoOAuth2;
import mokindang.jubging.project_backend.member.domain.LoginState;
import mokindang.jubging.project_backend.member.domain.Member;
import mokindang.jubging.project_backend.member.service.MemberService;
import mokindang.jubging.project_backend.member.service.request.AuthorizationCodeRequest;
import mokindang.jubging.project_backend.member.service.response.JwtResponse;
import mokindang.jubging.project_backend.member.service.response.KakaoApiMemberResponse;
import mokindang.jubging.project_backend.member.service.response.KakaoLoginResponse;
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

import static mokindang.jubging.project_backend.member.domain.vo.ProfileImage.DEFAULT_PROFILE_IMAGE_URL;
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
    @DisplayName("DB에 email이 없을 시 회원가입 flow를 진행한다. access token, refresh token, email 앞 4자리, alias, region, profileImageUrl, 로그인 상태 JOIN 을 반환한다.")
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
        softly.assertThat(kakaoLoginResponse.getProfileImageUrl()).isEqualTo(DEFAULT_PROFILE_IMAGE_URL);
        softly.assertThat(kakaoLoginResponse.getLoginState()).isEqualTo(LoginState.JOIN);
        softly.assertAll();
        verify(refreshTokenRepository, times(1)).save(any());

    }

    @Test
    @DisplayName("DB에 email 존재할 시 로그인 flow를 진행한다. access token, refresh token, email 앞 4자리, alias, region, profileImageUrl, 로그인 상태 LOGIN 을 반환한다.")
    void authenticateLogin() {
        //given
        Member existMember = new Member("dog123@test.com", "minho");
        existMember.updateRegion("동작구");
        existMember.updateProfileImage("https://testimage.png");
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
        softly.assertThat(kakaoLoginResponse.getRegion()).isEqualTo("동작구");
        softly.assertThat(kakaoLoginResponse.getProfileImageUrl()).isEqualTo("https://testimage.png");
        softly.assertThat(kakaoLoginResponse.getLoginState()).isEqualTo(LoginState.LOGIN);
        softly.assertAll();
        verify(refreshTokenRepository, times(1)).save(any());

    }

    @Test
    @DisplayName("RefreshToken을 입력 받아 유효한지 확인, 유효한 경우 Access Token, Refresh Token 재발행한다.")
    void reissue() {
        //given
        Member member = new Member("koho1047@naver.com", "고민호");

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
    @DisplayName("RefreshToken을 입력 받아 유효한지 확인, 유효하지 않은 경우 예외를 반환한다.")
    void reissueError() {
        //given
        when(refreshTokenRepository.findByToken(anyString())).thenThrow(new JwtException("Refresh Token 이 존재하지 않습니다."));

        //when, then
        Assertions.assertThatThrownBy(() -> authenticationService.reissue("Test Refresh Token")).isInstanceOf(JwtException.class)
                .hasMessage("Refresh Token 이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("로그아웃 시 일치하는 refreshToken 삭제를 수행한다.")
    void logout(){
        //given
        Member member = mock(Member.class);

        //when
        authenticationService.logout(member.getId());

        //then
        verify(refreshTokenRepository, times(1)).deleteAllByMemberId(member.getId());
    }
}
