package mokindang.jubging.project_backend.member;

import mokindang.jubging.project_backend.member.domain.Member;
import mokindang.jubging.project_backend.member.domain.vo.ProfileImage;
import mokindang.jubging.project_backend.member.domain.vo.Region;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockedMemberFactory {

    public static Member createMockedMember(final Long memberId) {
        Member member = mock(Member.class);
        when(member.getId()).thenReturn(memberId);
        when(member.getRegion()).thenReturn(Region.from("동작구"));
        when(member.getProfileImage()).thenReturn(new ProfileImage("test_url"));
        return member;
    }
}
