package mokindang.jubging.project_backend.controller.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import mokindang.jubging.project_backend.domain.region.vo.Region;
import mokindang.jubging.project_backend.repository.member.MemberRepository;
import mokindang.jubging.project_backend.service.member.MemberService;
import mokindang.jubging.project_backend.service.member.request.RegionUpdateRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    @MockBean
    private MemberRepository memberRepository;

    @Test
    @DisplayName("대한민국 영토 범위 안의 위도와 경도 입력 시 이에 대응하는 Region을 반환한다.")
    void callRegion() throws Exception {
        //given
        Region region = Region.createByDefaultValue();
        region.updateRegion("동작구");
        when(memberService.updateRegion(any(RegionUpdateRequest.class), anyLong())).thenReturn(region);

        RegionUpdateRequest regionUpdateRequest = new RegionUpdateRequest(126.95389562345368, 37.496322794913326);

        //when
        ResultActions actual = mockMvc.perform(put("/api/member/region")
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
        Region region = Region.createByDefaultValue();
        region.updateRegion("동작구");

        RegionUpdateRequest regionRequest = new RegionUpdateRequest(115.13181351, 37.496322794913326);

        when(memberService.updateRegion(any(RegionUpdateRequest.class), anyLong())).thenReturn(region);

        //when
        ResultActions resultActions = mockMvc.perform(put("/api/member/region")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regionRequest)));

        //then
        resultActions.andExpect(jsonPath("$.error").value("longitude(경도)는 대한민국 영토 범위 124~132 내의 값이어야 합니다."));
    }

    @Test
    @DisplayName("longitude(경도)가 대한민국 영토 범위 124~132에 포함되지 않는 값이 들어온 경우 예외를 반환한다.")
    void validateLatitude() throws Exception {
        //given
        Region region = Region.createByDefaultValue();
        region.updateRegion("동작구");
        when(memberService.updateRegion(any(RegionUpdateRequest.class), anyLong())).thenReturn(region);

        RegionUpdateRequest regionRequest = new RegionUpdateRequest(126.95389562345368, 50.335618198);

        //when
        ResultActions resultActions = mockMvc.perform(put("/api/member/region")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regionRequest)));

        //then
        resultActions.andExpect(jsonPath("$.error").value("latitude(위도)는 대한민국 영토 범위 33~39 내의 값이어야 합니다."));
    }
}
