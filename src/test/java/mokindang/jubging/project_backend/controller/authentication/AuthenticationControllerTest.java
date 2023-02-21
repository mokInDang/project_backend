package mokindang.jubging.project_backend.controller.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import mokindang.jubging.project_backend.domain.member.LoginState;
import mokindang.jubging.project_backend.service.dto.JwtResponse;
import mokindang.jubging.project_backend.service.dto.LoginStateDto;
import mokindang.jubging.project_backend.service.dto.RefreshTokenRequest;
import mokindang.jubging.project_backend.service.member.MemberService;
import mokindang.jubging.project_backend.web.jwt.TokenManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    @MockBean
    private TokenManager tokenManager;

    @MockBean
    private JwtResponse jwtResponse;



    @Test
    @DisplayName("회원가입 시 HTTP 상태코드는 201(CREATED)이며 Alias를 JSON으로 반환한다. Access Token 과 Refresh Token 은 Authorization 헤더에 담아 반환한다.")
    void joinAndState201() throws Exception{
        //given
        LoginStateDto loginStateDto = new LoginStateDto("Test Access Token", "Test Refresh Token", "Test Alias", LoginState.JOIN);

        when(memberService.login(any())).thenReturn(loginStateDto);

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
        LoginStateDto loginStateDto = new LoginStateDto("Test Access Token", "Test Refresh Token", "Test Alias", LoginState.LOGIN);

        when(memberService.login(any())).thenReturn(loginStateDto);

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

        when(memberService.reissue(any(RefreshTokenRequest.class))).thenReturn(jwtResponse);
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

        when(memberService.reissue(any(RefreshTokenRequest.class))).thenThrow(new JwtException("Refresh Token 이 존재하지 않습니다."));

        //when
        ResultActions resultActions = mockMvc.perform(post("/api/member/reissueToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshTokenRequest)));

        //then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Refresh Token 이 존재하지 않습니다."));
    }

}