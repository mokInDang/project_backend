package mokindang.jubging.project_backend.authentication.service;

import mokindang.jubging.project_backend.member.service.response.KakaoApiMemberResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class KaKaoOAuth2 {

    @Value("${kakao.client-id}")
    private String clientId ;

    public KakaoApiMemberResponse getMemberDto(String authorizationCode) {
        String accessToken = callKakaoApiToken(authorizationCode);

        return getMemberInformation(accessToken);
    }

    public String callKakaoApiToken(String authorizationCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", "https://dongnejupging.xyz/api/auth/join");
        params.add("code", authorizationCode);

        RestTemplate rt = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(params, headers);

        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        String tokenJson = response.getBody();
        JSONObject rjson = new JSONObject(tokenJson);
        String kakaoAccessToken = rjson.getString("access_token");

        return kakaoAccessToken;
    }

    public KakaoApiMemberResponse getMemberInformation(String kakaoAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + kakaoAccessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        RestTemplate rt = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);

        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        JSONObject body = new JSONObject(response.getBody());
        String email = body.getJSONObject("kakao_account").getString("email");
        String alias = body.getJSONObject("properties").getString("nickname");

        return new KakaoApiMemberResponse(email, alias);
    }
}
