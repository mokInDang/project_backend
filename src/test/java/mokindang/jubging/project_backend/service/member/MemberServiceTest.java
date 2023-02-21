package mokindang.jubging.project_backend.service.member;

import io.jsonwebtoken.JwtException;
import mokindang.jubging.project_backend.domain.member.Member;
import mokindang.jubging.project_backend.domain.member.LoginState;
import mokindang.jubging.project_backend.domain.token.RefreshToken;
import mokindang.jubging.project_backend.repository.member.MemberRepository;
import mokindang.jubging.project_backend.repository.token.RefreshTokenRepository;
import mokindang.jubging.project_backend.security.kakao.KaKaoOAuth2;
import mokindang.jubging.project_backend.service.dto.JwtResponse;
import mokindang.jubging.project_backend.service.dto.KakaoApiMemberResponse;
import mokindang.jubging.project_backend.service.dto.LoginStateDto;
import mokindang.jubging.project_backend.service.dto.RefreshTokenRequest;
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
class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private KaKaoOAuth2 kakaoOAuth2;

    @Mock
    private TokenManager tokenManager;

    @Mock
    private RefreshToken refreshToken;

    @InjectMocks
    private MemberService memberService;

    private final SoftAssertions softly = new SoftAssertions();

    @Test
    @DisplayName("DB에 email 존재 안할 시 LoginState는 JOIN이다. 회원가입 전 refresh 토큰이 없는 상태로 새로운 access, refresh 토큰을 생성하여 반환한다.")
    void authenticateJoin() {
        //given
        Member member = new Member("koho1047@naver.com", "고민호");
        KakaoApiMemberResponse kakaoApiMemberResponse = new KakaoApiMemberResponse("Kakao Test Access Token",
                "Kakao Test Refresh Token", "고민호", "koho1047@naver.com");

        when(kakaoOAuth2.getMemberDto(any())).thenReturn(kakaoApiMemberResponse);
        when(memberRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(memberRepository.save(any())).thenReturn(member);
        when(tokenManager.createToken(any())).thenReturn("Test Access Token");

        //when
        LoginStateDto loginStateDto = memberService.login("Test AuthorizedCode");

        //then
        softly.assertThat(loginStateDto.getLoginState()).isEqualTo(LoginState.JOIN);
        softly.assertThat(loginStateDto.getAccessToken()).isNotEmpty();
        softly.assertThat(loginStateDto.getRefreshToken()).isNotEmpty();
        softly.assertThat(loginStateDto.getAccessToken()).isNotEqualTo("Kakao Test Access Token");
        softly.assertThat(loginStateDto.getRefreshToken()).isNotEqualTo("Kakao Test Refresh Token");
        softly.assertAll();
        verify(refreshTokenRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("DB에 email 존재할 시 LoginState는 LOGIN이다. refresh 토큰이 있으면 토큰을 재발행하여 반환한다.")
    void authenticateLoginWhenHaveRefreshToken() {
        //given
        Member member = new Member("koho1047@naver.com", "고민호");
        KakaoApiMemberResponse kakaoApiMemberResponse = new KakaoApiMemberResponse("testAccessToken",
                "testRefreshToken", "고민호", "koho1047@naver.com");

        when(kakaoOAuth2.getMemberDto(any())).thenReturn(kakaoApiMemberResponse);
        when(memberRepository.findByEmail(any())).thenReturn(Optional.of(member));
        when(tokenManager.createToken(member.getId())).thenReturn("Test Access Token");
        when(refreshTokenRepository.findByMemberId(member.getId())).thenReturn(Optional.of(refreshToken));

        //when
        LoginStateDto loginStateDto = memberService.login("Test AuthorizedCode");

        //then
        softly.assertThat(loginStateDto.getLoginState()).isEqualTo(LoginState.LOGIN);
        softly.assertThat(loginStateDto.getAccessToken()).isNotEmpty();
        softly.assertThat(loginStateDto.getRefreshToken()).isNotEmpty();
        softly.assertAll();
        verify(refreshToken, times(1)).switchRefreshToken(any(), any());

    }

    @Test
    @DisplayName("DB에 email 존재할 시 LoginState는 LOGIN이다. DB에 refresh 토큰이 삭제되어 없는경우 토큰을 생성하여 반환한다.")
    void authenticateLoginWhenHaveNoRefreshToken() {
        //given
        Member member = new Member("koho1047@naver.com", "고민호");
        KakaoApiMemberResponse kakaoApiMemberResponse = new KakaoApiMemberResponse("testAccessToken",
                "testRefreshToken", "고민호", "koho1047@naver.com");

        when(kakaoOAuth2.getMemberDto(any())).thenReturn(kakaoApiMemberResponse);
        when(memberRepository.findByEmail(any())).thenReturn(Optional.of(member));
        when(tokenManager.createToken(any())).thenReturn("Test Access Token");

        //when
        LoginStateDto loginStateDto = memberService.login("Test AuthorizedCode");

        //then
        softly.assertThat(loginStateDto.getLoginState()).isEqualTo(LoginState.LOGIN);
        softly.assertThat(loginStateDto.getAccessToken()).isNotEmpty();
        softly.assertThat(loginStateDto.getRefreshToken()).isNotEmpty();
        softly.assertAll();
        verify(refreshTokenRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("RefreshToken을 입력 받아 유효한지 확인, 유효한 경우 Access Token, Refresh Token 재발행한다.")
    void reissue() {
        //given
        Member member = new Member("koho1047@naver.com", "고민호");
        RefreshTokenRequest requestRefreshToken = new RefreshTokenRequest("Request Refresh Token");

        when(refreshTokenRepository.findByToken(any())).thenReturn(Optional.of(refreshToken));
        when(memberRepository.findById(any())).thenReturn(Optional.of(member));
        when(tokenManager.createToken(any())).thenReturn("Test Access Token");

        //when
        JwtResponse reissue = memberService.reissue(requestRefreshToken);

        //then
        softly.assertThat(reissue.getAccessToken()).isEqualTo("Test Access Token");
        softly.assertThat(reissue.getRefreshToken()).isNotEqualTo("Request Refresh Token");
        softly.assertAll();
        verify(refreshToken, times(1)).switchRefreshToken(any(), any());
    }

    @Test
    @DisplayName("RefreshToken을 입력 받아 유효한지 확인, 유효하지 않은 경우 예외를 반환한다.")
    void reissueError() {
        //given
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest("Request Refresh Token");
        when(refreshTokenRepository.findByToken(anyString())).thenThrow(new JwtException("Refresh Token 이 존재하지 않습니다."));

        //when, then
        Assertions.assertThatThrownBy(() -> memberService.reissue(refreshTokenRequest)).isInstanceOf(JwtException.class)
                .hasMessage("Refresh Token 이 존재하지 않습니다.");
    }
}
