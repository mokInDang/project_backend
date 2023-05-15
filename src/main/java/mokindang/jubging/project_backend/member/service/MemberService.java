package mokindang.jubging.project_backend.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mokindang.jubging.project_backend.member.domain.Member;
import mokindang.jubging.project_backend.member.domain.vo.Region;
import mokindang.jubging.project_backend.member.repository.MemberRepository;
import mokindang.jubging.project_backend.member.service.request.MyPageEditRequest;
import mokindang.jubging.project_backend.member.service.request.RegionUpdateRequest;
import mokindang.jubging.project_backend.member.service.response.MyPageResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final KaKaoLocalApi kakaoLocalApi;
    private final MemberRepository memberRepository;

    public Member saveMember(Member member) {
        return memberRepository.save(member);
    }

    public Optional<Member> findByMemberEmail(String memberEmail) {
        return memberRepository.findByEmail(memberEmail);
    }

    public Member findByMemberId(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 유저가 존재하지 않습니다."));
    }

    @Transactional
    public Region updateRegion(final RegionUpdateRequest regionUpdateRequest, final Long memberId) {
        Member member = findByMemberId(memberId);
        member.updateRegion(kakaoLocalApi.switchCoordinateToRegion(regionUpdateRequest));
        return member.getRegion();
    }

    public MyPageResponse getMyInformation(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 유저가 존재하지 않습니다."));
        return new MyPageResponse(member.getProfileImage().getProfileImageUrl(), member.getAlias(), member.getRegion().getValue());
    }

    @Transactional
    public MyPageResponse editMyPage(final Long memberId, final MyPageEditRequest myPageEditRequest) {
        Member member = findByMemberId(memberId);
        member.updateProfileImage(myPageEditRequest.getProfileImageUrl());
        member.updateAlias(myPageEditRequest.getAlias());
        return new MyPageResponse(member.getProfileImage().getProfileImageUrl(), member.getAlias(), member.getRegion().getValue());
    }
}
