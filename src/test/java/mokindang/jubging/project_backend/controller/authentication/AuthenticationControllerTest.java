package mokindang.jubging.project_backend.controller.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import mokindang.jubging.project_backend.domain.member.LoginState;
import mokindang.jubging.project_backend.service.authentication.AuthenticationService;
import mokindang.jubging.project_backend.service.member.request.RefreshTokenRequest;
import mokindang.jubging.project_backend.service.member.request.RegionRequest;
import mokindang.jubging.project_backend.service.member.response.JwtResponse;
import mokindang.jubging.project_backend.service.member.response.KakaoLoginResponse;
import mokindang.jubging.project_backend.web.jwt.TokenManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthenticationController.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private TokenManager tokenManager;

    @MockBean
    private JwtResponse jwtResponse;



    @Test
    @DisplayName("회원가입 시 HTTP 상태코드는 201(CREATED)이며 Alias를 JSON으로 반환한다. Access Token 과 Refresh Token 은 Authorization 헤더에 담아 반환한다.")
    void joinAndState201() throws Exception{
        //given
        KakaoLoginResponse kakaoLoginResponse = new KakaoLoginResponse("Test Access Token", "Test Refresh Token", "Test Alias", LoginState.JOIN);

        when(authenticationService.login(any())).thenReturn(kakaoLoginResponse);

        //when
        ResultActions resultActions = mockMvc.perform(get("/api/member/join?code=testcode")
                                                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.alias").value("Test Alias"))
                .andExpect(header().exists("Authorization"));
    }

    @Test
    @DisplayName("로그인 시 HTTP 상태코드는 200(OK)이며 Alias를 JSON으로 반환한다. Access Token 과 Refresh Token 은 Authorization 헤더에 담아 반환한다.")
    void loginAndState200() throws Exception{
        //given
        KakaoLoginResponse kakaoLoginResponse = new KakaoLoginResponse("Test Access Token", "Test Refresh Token", "Test Alias", LoginState.LOGIN);

        when(authenticationService.login(any())).thenReturn(kakaoLoginResponse);

        //when
        ResultActions resultActions = mockMvc.perform(get("/api/member/join?code=testcode")
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.alias").value("Test Alias"))
                .andExpect(header().exists("Authorization"));
    }

    @Test
    @DisplayName("Refresh Token 재발급 요청 시 DB에 해당 Refresh Token 존재하면 새로운 Access Token, Refresh Token 을 재발급 및 상태코드 200(OK)을 반환한다.")
    void reissue() throws Exception{
        //given
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest("Refresh Token");

        when(authenticationService.reissue(any(RefreshTokenRequest.class))).thenReturn(jwtResponse);
        when(jwtResponse.getAccessToken()).thenReturn("New Access Token");
        when(jwtResponse.getRefreshToken()).thenReturn("New Refresh Token");

        //when
        ResultActions resultActions = mockMvc.perform(post("/api/member/reissueToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshTokenRequest)));


        //then
        resultActions.andExpect(status().isOk())
                .andExpect(header().exists("Authorization"))
                .andExpect(header().stringValues("Authorization", "New Access Token", "New Refresh Token"));
    }

    @Test
    @DisplayName("Refresh Token 재발급 요청 시 DB에 해당 Refresh Token이 DB에 존재하지 않으면 상태코드 400 및 예외를 반환한다.")
    void reissueError() throws Exception {
        //given
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest("Refresh Token");

        when(authenticationService.reissue(any(RefreshTokenRequest.class))).thenThrow(new JwtException("Refresh Token 이 존재하지 않습니다."));

        //when
        ResultActions resultActions = mockMvc.perform(post("/api/member/reissueToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshTokenRequest)));

        //then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Refresh Token 이 존재하지 않습니다."));
    }

    @Test
    @DisplayName("대한민국 영토 범위 안의 위도와 경도 입력 시 이에 대응하는 지역을 반환한다.")
    void callRegion() throws Exception {
        //given
        RegionRequest regionRequest = new RegionRequest(126.95389562345368, 37.496322794913326);

        when(authenticationService.getRegion(any(), any())).thenReturn("동작구");

        //when
        ResultActions resultActions = mockMvc.perform(put("/regionAuth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regionRequest)));

        //then
        resultActions.andExpect(jsonPath("$.region").value("동작구"));
    }

    @Test
    @DisplayName("longitude(경도)는 대한민국 영토 범위 124~132에 포함되지 않는 값이 들어온 경우 예외를 반환한다.")
    void validateLongitude() throws Exception {
        //given
        RegionRequest regionRequest = new RegionRequest(115.13181351, 37.496322794913326);

        when(authenticationService.getRegion(any(), any())).thenReturn("동작구");

        //when
        ResultActions resultActions = mockMvc.perform(put("/regionAuth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regionRequest)));

        //then
        resultActions.andExpect(jsonPath("$.error").value("longitude(경도)는 대한민국 영토 범위 124~132 내의 값이어야 합니다."));
    }

    @Test
    @DisplayName("longitude(경도)는 대한민국 영토 범위 124~132에 포함되지 않는 값이 들어온 경우 예외를 반환한다.")
    void validateLatitude() throws Exception {
        //given
        RegionRequest regionRequest = new RegionRequest(126.95389562345368, 50.335618198);

        when(authenticationService.getRegion(any(), any())).thenReturn("동작구");

        //when
        ResultActions resultActions = mockMvc.perform(put("/regionAuth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regionRequest)));

        //then
        resultActions.andExpect(jsonPath("$.error").value("latitude(위도)는 대한민국 영토 범위 33~39 내의 값이어야 합니다."));
    }

}
