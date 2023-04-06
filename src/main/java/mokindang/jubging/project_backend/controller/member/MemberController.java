package mokindang.jubging.project_backend.controller.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mokindang.jubging.project_backend.domain.member.vo.Region;
import mokindang.jubging.project_backend.service.file.FileResponse;
import mokindang.jubging.project_backend.service.member.MemberService;
import mokindang.jubging.project_backend.service.member.request.RegionUpdateRequest;
import mokindang.jubging.project_backend.service.member.response.MyPageResponse;
import mokindang.jubging.project_backend.service.member.response.RegionUpdateResponse;
import mokindang.jubging.project_backend.web.argumentresolver.Login;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController implements MemberControllerSwagger{

    private final MemberService memberService;

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

    @PostMapping("/profile-image")
    public ResponseEntity<FileResponse> uploadProfileImage(@Login Long memberId, @RequestPart(value = "file") MultipartFile multipartFile) {
        FileResponse fileResponse = memberService.updateProfileImage(memberId, multipartFile);
        return ResponseEntity.ok()
                .body(fileResponse);
    }
}