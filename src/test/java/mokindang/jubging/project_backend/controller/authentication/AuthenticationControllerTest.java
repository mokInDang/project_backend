package mokindang.jubging.project_backend.controller.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import mokindang.jubging.project_backend.domain.member.LoginState;
import mokindang.jubging.project_backend.service.authentication.AuthenticationService;
import mokindang.jubging.project_backend.service.member.request.AuthorizationCodeRequest;
import mokindang.jubging.project_backend.service.member.response.JwtResponse;
import mokindang.jubging.project_backend.service.member.response.KakaoLoginResponse;
import mokindang.jubging.project_backend.web.jwt.TokenManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthenticationController.class)
class AuthenticationControllerTest {

    public static final String TEST_REFRESH_TOKEN = "f6341354-be83-11ed-afa1-0242ac120002";
    public static final String TEST_NEW_REFRESH_TOKEN = "9ecfd4fe-bee2-11ed-afa1-0242ac120002";
    public static final String TEST_ACCESS_TOKEN = "Test Access Token";
    public static final String TEST_NEW_ACCESS_TOKEN = "Test New Access Token";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private TokenManager tokenManager;

    @Test
    @DisplayName("회원가입 시 HTTP 상태코드는 201(CREATED)이다. Access Token은 Authorization, Refresh Token은 Set-Cookie 헤더에 담아 응답한다.")
    void joinAndState201() throws Exception {
        //given
        KakaoLoginResponse kakaoLoginResponse = new KakaoLoginResponse(TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN, "dog1", "minho", "동작구", LoginState.JOIN);
        AuthorizationCodeRequest authorizationCodeRequest = new AuthorizationCodeRequest("Test Authorization Code");

        when(authenticationService.login(any())).thenReturn(kakaoLoginResponse);

        //when
        ResultActions resultActions = mockMvc.perform(post("/api/member/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authorizationCodeRequest)));

        //then
        resultActions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("dog1"))
                .andExpect(jsonPath("$.alias").value("minho"))
                .andExpect(jsonPath("$.region").value("동작구"))
                .andExpect(header().exists(HttpHeaders.AUTHORIZATION))
                .andExpect(header().stringValues(HttpHeaders.SET_COOKIE,"refreshToken=f6341354-be83-11ed-afa1-0242ac120002; Path=/; Secure; HttpOnly"));
    }

    @Test
    @DisplayName("로그인 시 HTTP 상태코드는 200(OK)이며 Alias를 JSON으로 반환한다. Access Token은 Authorization, Refresh Token은 Set-Cookie 헤더에 담아 응답한다.")
    void loginAndState200() throws Exception {
        //given
        KakaoLoginResponse kakaoLoginResponse = new KakaoLoginResponse(TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN, "dog1", "minho", "동작구", LoginState.LOGIN);
        AuthorizationCodeRequest authorizationCodeRequest = new AuthorizationCodeRequest("Test Authorization Code");

        when(authenticationService.login(any())).thenReturn(kakaoLoginResponse);

        //when
        ResultActions resultActions = mockMvc.perform(post("/api/member/join?code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authorizationCodeRequest)));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("dog1"))
                .andExpect(jsonPath("$.alias").value("minho"))
                .andExpect(jsonPath("$.region").value("동작구"))
                .andExpect(header().exists(HttpHeaders.AUTHORIZATION))
                .andExpect(header().stringValues(HttpHeaders.SET_COOKIE,"refreshToken=f6341354-be83-11ed-afa1-0242ac120002; Path=/; Secure; HttpOnly"));
    }

    @Test
    @DisplayName("Refresh Token 재발급 요청 시 DB에 해당 Refresh Token 존재하면 새로운 Access Token, Refresh Token 을 재발급 및 상태코드 200(OK)을 반환한다.")
    void reissue() throws Exception {
        //given
        JwtResponse jwtResponse = new JwtResponse(TEST_NEW_ACCESS_TOKEN, TEST_NEW_REFRESH_TOKEN);

        when(authenticationService.reissue(any())).thenReturn(jwtResponse);

        //when
        ResultActions resultActions = mockMvc.perform(post("/api/member/reissueToken")
                .header(HttpHeaders.SET_COOKIE,TEST_REFRESH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON));


        //then
        resultActions.andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.AUTHORIZATION))
                .andExpect(header().stringValues(HttpHeaders.SET_COOKIE,"refreshToken=9ecfd4fe-bee2-11ed-afa1-0242ac120002; Path=/; Secure; HttpOnly"));
    }

    @Test
    @DisplayName("Refresh Token 재발급 요청 시 DB에 해당 Refresh Token이 DB에 존재하지 않으면 상태코드 400 및 예외를 반환한다.")
    void reissueError() throws Exception {
        //given
        when(authenticationService.reissue(any())).thenThrow(new JwtException("Refresh Token 이 존재하지 않습니다."));

        //when
        ResultActions resultActions = mockMvc.perform(post("/api/member/reissueToken")
                .header(HttpHeaders.SET_COOKIE,TEST_REFRESH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Refresh Token 이 존재하지 않습니다."));
    }
}
