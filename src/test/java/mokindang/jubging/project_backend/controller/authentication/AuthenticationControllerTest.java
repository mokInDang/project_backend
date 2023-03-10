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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.servlet.http.Cookie;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.SET_COOKIE;
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
    @DisplayName("???????????? ??? HTTP ??????????????? 201(CREATED)??????. Access Token??? Authorization, Refresh Token??? Set-Cookie ????????? ?????? ????????????.")
    void joinAndState201() throws Exception {
        //given
        KakaoLoginResponse kakaoLoginResponse = new KakaoLoginResponse(TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN, "dog1", "minho", "?????????", LoginState.JOIN);
        AuthorizationCodeRequest authorizationCodeRequest = new AuthorizationCodeRequest("Test Authorization Code");

        when(authenticationService.login(any())).thenReturn(kakaoLoginResponse);

        //when
        ResultActions resultActions = mockMvc.perform(post("/api/auth/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authorizationCodeRequest)));

        //then
        resultActions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("dog1"))
                .andExpect(jsonPath("$.alias").value("minho"))
                .andExpect(jsonPath("$.region").value("?????????"))
                .andExpect(header().exists(AUTHORIZATION))
                .andExpect(header().exists(SET_COOKIE));
    }

    @Test
    @DisplayName("????????? ??? HTTP ??????????????? 200(OK)?????? Alias??? JSON?????? ????????????. Access Token??? Authorization, Refresh Token??? Set-Cookie ????????? ?????? ????????????.")
    void loginAndState200() throws Exception {
        //given
        KakaoLoginResponse kakaoLoginResponse = new KakaoLoginResponse(TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN, "dog1", "minho", "?????????", LoginState.LOGIN);
        AuthorizationCodeRequest authorizationCodeRequest = new AuthorizationCodeRequest("Test Authorization Code");

        when(authenticationService.login(any())).thenReturn(kakaoLoginResponse);

        //when
        ResultActions resultActions = mockMvc.perform(post("/api/auth/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authorizationCodeRequest)));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("dog1"))
                .andExpect(jsonPath("$.alias").value("minho"))
                .andExpect(jsonPath("$.region").value("?????????"))
                .andExpect(header().exists(AUTHORIZATION))
                .andExpect(header().exists(SET_COOKIE));

    }

    @Test
    @DisplayName("Refresh Token ????????? ?????? ??? DB??? ?????? Refresh Token ???????????? ????????? Access Token, Refresh Token ??? ????????? ??? ???????????? 200(OK)??? ????????????.")
    void reissue() throws Exception {
        //given
        JwtResponse jwtResponse = new JwtResponse(TEST_NEW_ACCESS_TOKEN, TEST_NEW_REFRESH_TOKEN);

        when(authenticationService.reissue(any())).thenReturn(jwtResponse);

        //when
        ResultActions resultActions = mockMvc.perform(post("/api/auth/refresh")
                .cookie(new Cookie("refreshToken", TEST_REFRESH_TOKEN))
                .contentType(MediaType.APPLICATION_JSON));


        //then
        resultActions.andExpect(status().isOk())
                .andExpect(header().exists(AUTHORIZATION))
                .andExpect(header().exists(SET_COOKIE));
    }

    @Test
    @DisplayName("Refresh Token ????????? ?????? ??? DB??? ?????? Refresh Token??? DB??? ???????????? ????????? ???????????? 400 ??? ????????? ????????????.")
    void reissueError() throws Exception {
        //given
        when(authenticationService.reissue(any())).thenThrow(new JwtException("Refresh Token ??? ???????????? ????????????."));

        //when
        ResultActions resultActions = mockMvc.perform(post("/api/auth/refresh")
                .cookie(new Cookie("refreshToken", TEST_REFRESH_TOKEN))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Refresh Token ??? ???????????? ????????????."));
    }
}
