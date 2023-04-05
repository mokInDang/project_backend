package mokindang.jubging.project_backend.controller.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mokindang.jubging.project_backend.domain.member.Member;
import mokindang.jubging.project_backend.domain.member.vo.Region;
import mokindang.jubging.project_backend.service.file.FileResponse;
import mokindang.jubging.project_backend.service.file.FileService;
import mokindang.jubging.project_backend.service.member.MemberService;
import mokindang.jubging.project_backend.service.member.request.RegionUpdateRequest;
import mokindang.jubging.project_backend.service.member.response.MyPageResponse;
import mokindang.jubging.project_backend.service.member.response.RegionUpdateResponse;
import mokindang.jubging.project_backend.web.argumentresolver.Login;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import static mokindang.jubging.project_backend.domain.member.vo.ProfileImage.DEFAULT_PROFILE_IMAGE_NAME;
import static mokindang.jubging.project_backend.domain.member.vo.ProfileImage.DEFAULT_PROFILE_IMAGE_URL;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController implements MemberControllerSwagger{

    private final MemberService memberService;
    private final FileService fileService;

    @PatchMapping("/region")
    public ResponseEntity<RegionUpdateResponse> updateRegion(@Login Long memberId, @Valid @RequestBody RegionUpdateRequest regionUpdateRequest) {
        Region updateRegion = memberService.updateRegion(regionUpdateRequest, memberId);
        return ResponseEntity.ok()
                .body(new RegionUpdateResponse(updateRegion.getValue()));
    }

    @GetMapping("/mypage")
    public ResponseEntity<MyPageResponse> callMyPage(@Login Long memberId) {
        MyPageResponse myPageResponse = memberService.getMyInformation(memberId);
        return ResponseEntity.ok()
                .body(myPageResponse);
    }

    @PostMapping("/upload")
    public ResponseEntity<FileResponse> uploadProfileImage(@Login Long memberId, @RequestPart(value = "file") MultipartFile multipartFile) {
        Member member = memberService.findByMemberId(memberId);

        FileResponse fileResponse = fileService.uploadFile(multipartFile);
        log.info("memberId = {}, alias = {} 의 프로필 이미지 {} 업로드", member.getId(), member.getAlias(), fileResponse.getUploadFileName());

        if (!member.getProfileImage().getProfileImageUrl().equals(DEFAULT_PROFILE_IMAGE_URL) && !member.getProfileImage().getProfileImageName().equals(DEFAULT_PROFILE_IMAGE_NAME)) {
            fileService.deleteFile(member.getProfileImage().getProfileImageName());
            log.info("memberId = {}, alias = {} 의 이전 프로필 이미지 {} 삭제", member.getId(), member.getAlias(), member.getProfileImage().getProfileImageName());
        }

        memberService.updateProfileImage(member, fileResponse.getUploadFileUrl(), fileResponse.getUploadFileName());

        return ResponseEntity.ok()
                .body(fileResponse);
    }
}