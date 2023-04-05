package mokindang.jubging.project_backend.service.member;

import mokindang.jubging.project_backend.domain.member.Member;
import mokindang.jubging.project_backend.domain.region.vo.Region;
import mokindang.jubging.project_backend.repository.member.MemberRepository;
import mokindang.jubging.project_backend.service.member.request.RegionUpdateRequest;
import mokindang.jubging.project_backend.service.member.response.MyPageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private KaKaoLocalApi kaKaoLocalApi;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("Member 를 저장한다.")
    void saveMember() {
        //given
        Member member = new Member("test@emial.com", "test");

        //when
        memberService.saveMember(member);

        //then
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("memberId로 조회 시 존재하지 않는 member 를 조회하면 예외를 터트린다.")
    void findByMemberId() {
        //given
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> memberService.findByMemberId(1L)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당하는 유저가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("member email 조회 시  member 를 조회한다.")
    void findByMemberEmail() {
        //given
        Member member = mock(Member.class);
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(member));

        //when
        memberService.findByMemberEmail("Test@Email.com");

        //then
        verify(memberRepository, times(1)).findByEmail(anyString());
    }

    @Test
    @DisplayName(" 유저로 부터 입력받은 위치 값을 카카오 api 를 통해 조회해 유저의 지역을 업데이트한다.")
    void updateRegion() {
        //given
        Member member = new Member("test@email.com", "test");
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(kaKaoLocalApi.switchCoordinateToRegion(any(RegionUpdateRequest.class))).thenReturn("동작구");

        RegionUpdateRequest regionUpdateRequest = new RegionUpdateRequest(124.123, 34.512);

        //when
        Region region = memberService.updateRegion(regionUpdateRequest, 1L);

        //then
        assertThat(region.getValue()).isEqualTo("동작구");
    }

    @Test
    @DisplayName("멤버의 alias, region, profileImageUrl을 MyPageResponse Dto에 담아 반환한다.")
    void getMyInformation(){
        //given
        Member member = new Member("test@email.com", "test");
        member.updateRegion("부천시");
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        //when
        MyPageResponse myInformation = memberService.getMyInformation(1L);

        //then
        assertThat(myInformation.getAlias()).isEqualTo("test");
        assertThat(myInformation.getRegion()).isEqualTo("부천시");
        assertThat(myInformation.getProfileImageUrl()).isEqualTo("DEFAULT_PROFILE_IMAGE_URL");
    }
}
