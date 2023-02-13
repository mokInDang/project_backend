package mokindang.jubging.project_backend.service.member;

import mokindang.jubging.project_backend.domain.member.Member;
import mokindang.jubging.project_backend.domain.member.MemberState;
import mokindang.jubging.project_backend.repository.member.MemberRepository;
import mokindang.jubging.project_backend.security.kakao.KaKaoOAuth2;
import mokindang.jubging.project_backend.service.member.dto.KakaoApiMemberResponse;
import mokindang.jubging.project_backend.service.member.dto.LoginStateDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private KaKaoOAuth2 kakaoOAuth2;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("DB에 email 존재 안할 시 회원가입 처리와 201 상태코드 반환하기 위해 MemberState가 JOIN이다.")
    public void authenticateJoin() {
        //given
        Member member = new Member("koho1047@naver.com", "고민호");
        KakaoApiMemberResponse memberDto = new KakaoApiMemberResponse("testAccessToken",
                "testRefreshToken", "고민호", "koho1047@naver.com");

        Mockito.when(memberRepository.findByEmail("koho1047@naver.com")).thenReturn(Optional.empty());
        Mockito.when(memberRepository.save(new Member("koho1047@naver.com", "고민호"))).thenReturn(member);
        Mockito.when(kakaoOAuth2.getMemberDto("testAuthorizationCode")).thenReturn(memberDto);

        //when
        LoginStateDto loginStateDto = memberService.login("testAuthorizationCode");

        //then
        Assertions.assertThat(loginStateDto.getMemberState()).isEqualTo(MemberState.JOIN);
    }

    @Test
    @DisplayName("DB에 email 존재 확인 시 로그인 처리와 200 상태코드를 반환하기 위해 MemberState가 LOGIN이다.")
    public void authenticateLogin() {
        //given
        Member member = new Member("koho1047@naver.com", "고민호");
        KakaoApiMemberResponse memberDto = new KakaoApiMemberResponse("testAccessToken",
                "testRefreshToken", "고민호", "koho1047@naver.com");

        Mockito.when(memberRepository.findByEmail("koho1047@naver.com")).thenReturn(Optional.ofNullable(member));
        Mockito.when(kakaoOAuth2.getMemberDto("testAuthorizationCode")).thenReturn(memberDto);

        //when
        LoginStateDto loginStateDto = memberService.login("testAuthorizationCode");

        //then
        Assertions.assertThat(loginStateDto.getMemberState()).isEqualTo(MemberState.LOGIN);
    }
}
