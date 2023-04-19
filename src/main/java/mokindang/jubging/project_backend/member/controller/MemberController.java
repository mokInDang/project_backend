package mokindang.jubging.project_backend.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mokindang.jubging.project_backend.member.domain.vo.Region;
import mokindang.jubging.project_backend.member.service.MemberService;
import mokindang.jubging.project_backend.member.service.request.MyPageEditRequest;
import mokindang.jubging.project_backend.member.service.request.RegionUpdateRequest;
import mokindang.jubging.project_backend.member.service.response.MyPageResponse;
import mokindang.jubging.project_backend.member.service.response.RegionUpdateResponse;
import mokindang.jubging.project_backend.web.argumentresolver.Login;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PatchMapping(value = "/edit-mypage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MyPageResponse> editMyPage(@Login Long memberId, @ModelAttribute MyPageEditRequest myPageEditRequest){
        MyPageResponse myPageResponse = memberService.editMypage(memberId, myPageEditRequest);
        return ResponseEntity.ok()
                .body(myPageResponse);
    }
}
