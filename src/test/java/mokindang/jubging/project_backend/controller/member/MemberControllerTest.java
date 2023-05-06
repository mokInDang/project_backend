package mokindang.jubging.project_backend.controller.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import mokindang.jubging.project_backend.member.controller.MemberController;
import mokindang.jubging.project_backend.member.domain.Member;
import mokindang.jubging.project_backend.member.domain.vo.Region;
import mokindang.jubging.project_backend.member.service.MemberService;
import mokindang.jubging.project_backend.member.service.request.MyPageEditRequest;
import mokindang.jubging.project_backend.member.service.request.RegionUpdateRequest;
import mokindang.jubging.project_backend.member.service.response.MyPageResponse;
import mokindang.jubging.project_backend.web.jwt.TokenManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TokenManager tokenManager;

    @MockBean
    private MemberService memberService;


    @Test
    @DisplayName("대한민국 영토 범위 안의 위도와 경도 입력 시 이에 대응하는 Region을 반환한다.")
    void callRegion() throws Exception {
        //given
        Region region = Region.from("동작구");
        when(memberService.updateRegion(any(RegionUpdateRequest.class), anyLong())).thenReturn(region);

        RegionUpdateRequest regionUpdateRequest = new RegionUpdateRequest(126.95389562345368, 37.496322794913326);

        //when
        ResultActions actual = mockMvc.perform(patch("/api/member/region")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regionUpdateRequest)));

        //then
        actual.andExpect(status().isOk())
                .andExpect(jsonPath("$.region").value("동작구"));
    }

    @Test
    @DisplayName("longitude(경도)가 대한민국 영토 범위 124~132에 포함되지 않는 값이 들어온 경우 예외를 반환한다.")
    void validateLongitude() throws Exception {
        //given
        Region region = Region.from("동작구");

        RegionUpdateRequest regionRequest = new RegionUpdateRequest(115.13181351, 37.496322794913326);

        when(memberService.updateRegion(any(RegionUpdateRequest.class), anyLong())).thenReturn(region);

        //when
        ResultActions resultActions = mockMvc.perform(patch("/api/member/region")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regionRequest)));

        //then
        resultActions.andExpect(jsonPath("$.error").value("longitude(경도)는 대한민국 영토 범위 124~132 내의 값이어야 합니다."));
    }

    @Test
    @DisplayName("longitude(경도)가 대한민국 영토 범위 124~132에 포함되지 않는 값이 들어온 경우 예외를 반환한다.")
    void validateLatitude() throws Exception {
        //given
        Region region = Region.from("동작구");
        when(memberService.updateRegion(any(RegionUpdateRequest.class), anyLong())).thenReturn(region);

        RegionUpdateRequest regionRequest = new RegionUpdateRequest(126.95389562345368, 50.335618198);

        //when
        ResultActions resultActions = mockMvc.perform(patch("/api/member/region")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regionRequest)));

        //then
        resultActions.andExpect(jsonPath("$.error").value("latitude(위도)는 대한민국 영토 범위 33~39 내의 값이어야 합니다."));
    }

    @Test
    @DisplayName("마이페이지 조회 시 alias, region, profileImageUrl을 반환한다.")
    void myPage() throws Exception {
        //given
        MyPageResponse mypageResponse = new MyPageResponse("https://test.png", "minho", "동작구");
        when(memberService.getMyInformation(any())).thenReturn(mypageResponse);

        //when
        ResultActions resultActions = mockMvc.perform(get("/api/member/mypage"));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.alias").value("minho"))
                .andExpect(jsonPath("$.region").value("동작구"))
                .andExpect(jsonPath("$.profileImageUrl").value("https://test.png"));
    }

    @Test
    @DisplayName("내정보수정 시 입력 된 이미지, 별칭으로 멤버를 업데이트한 후 업로드된 이미지의 url, 업데이트된 별칭, 멤버의 지역을 반환한다.")
    void editMyPage() throws Exception {
        //given
        Member member = new Member("test123@test.com", "minho");
        member.updateRegion("동작구");
        MyPageResponse myPageResponse = new MyPageResponse("https://test.png", "newAlias", "동작구");
        MyPageEditRequest myPageEditRequest = new MyPageEditRequest("https://test.png", "minho");
        when(memberService.editMypage(any(), any())).thenReturn(myPageResponse);

        //when
        ResultActions resultActions = mockMvc.perform(patch("/api/member/edit-mypage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(myPageEditRequest))
        );

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.profileImageUrl").value("https://test.png"))
                .andExpect(jsonPath("$.alias").value("newAlias"))
                .andExpect(jsonPath("$.region").value("동작구"));
    }
}
