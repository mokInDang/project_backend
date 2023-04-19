package mokindang.jubging.project_backend.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mokindang.jubging.project_backend.member.domain.Member;
import mokindang.jubging.project_backend.member.domain.vo.Region;
import mokindang.jubging.project_backend.member.repository.MemberRepository;
import mokindang.jubging.project_backend.file.FileResponse;
import mokindang.jubging.project_backend.file.FileService;
import mokindang.jubging.project_backend.member.service.request.MyPageEditRequest;
import mokindang.jubging.project_backend.member.service.request.RegionUpdateRequest;
import mokindang.jubging.project_backend.member.service.response.MyPageResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static mokindang.jubging.project_backend.file.FileService.PROFILE_IMAGE;

@Slf4j
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

    public MyPageResponse getMyInformation(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 유저가 존재하지 않습니다."));
        return new MyPageResponse(member.getProfileImage().getProfileImageUrl(), member.getAlias(), member.getRegion().getValue());
    }

    @Transactional
    public MyPageResponse editMypage(final Long memberId, final MyPageEditRequest myPageEditRequest) {
        Member member = findByMemberId(memberId);
        FileResponse fileResponse = updateProfileImage(member, myPageEditRequest.getProfileImage());
        member.updateAlias(myPageEditRequest.getAlias());
        return new MyPageResponse(fileResponse.getUploadFileUrl(), member.getAlias(), member.getRegion().getValue());
    }

    private FileResponse updateProfileImage(final Member member, final MultipartFile multipartFile) {
        FileResponse fileResponse = fileService.uploadFile(multipartFile, member);
        fileService.deleteFile(member.getProfileImage().getProfileImageName(), PROFILE_IMAGE);
        log.info("memberId = {}, alias = {} 의 이전 프로필 이미지 {} 삭제", member.getId(), member.getAlias(), member.getProfileImage().getProfileImageName());
        member.updateProfileImage(fileResponse.getUploadFileUrl(), fileResponse.getUploadFileName());
        return fileResponse;
    }
}
