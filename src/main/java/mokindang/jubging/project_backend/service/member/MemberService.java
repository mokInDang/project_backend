package mokindang.jubging.project_backend.service.member;

import lombok.RequiredArgsConstructor;
import mokindang.jubging.project_backend.domain.member.Member;
import mokindang.jubging.project_backend.domain.member.vo.Region;
import mokindang.jubging.project_backend.repository.member.MemberRepository;
import mokindang.jubging.project_backend.service.file.FileResponse;
import mokindang.jubging.project_backend.service.file.FileService;
import mokindang.jubging.project_backend.service.member.request.RegionUpdateRequest;
import mokindang.jubging.project_backend.service.member.response.MyPageResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final KaKaoLocalApi kakaoLocalApi;
    private final MemberRepository memberRepository;
    private final FileService fileService;

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

    @Transactional
    public FileResponse updateProfileImage(final Long memberId, final MultipartFile multipartFile) {
        Member member = findByMemberId(memberId);
        FileResponse fileResponse = fileService.uploadFile(multipartFile, member);
        fileService.deleteFile(member);
        member.updateProfileImage(fileResponse.getUploadFileUrl(), fileResponse.getUploadFileName());
        return fileResponse;
    }

    public MyPageResponse getMyInformation(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 유저가 존재하지 않습니다."));
        return new MyPageResponse(member.getAlias(), member.getRegion().getValue(), member.getProfileImage().getProfileImageUrl());
    }
}
