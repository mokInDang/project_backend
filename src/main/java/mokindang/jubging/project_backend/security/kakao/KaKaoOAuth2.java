package mokindang.jubging.project_backend.security.kakao;

import mokindang.jubging.project_backend.service.member.response.KakaoApiMemberResponse;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class KaKaoOAuth2 {

    public KakaoApiMemberResponse getMemberDto(String authorizationCode) {
        List<Object> tokens = callKakaoApiToken(authorizationCode);

        return getMemberInformation(tokens);
    }

    public List<Object> callKakaoApiToken(String authorizationCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "60b35611c843f6c8f618a495ecc8eaf6");
        params.add("redirect_uri", "https://www.dongnejupging.xyz/api/member/join");
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
        String accessToken = rjson.getString("access_token");
        String refreshToken = rjson.getString("refresh_token");

        List<Object> tokenList = new ArrayList<>();
        tokenList.add(accessToken);
        tokenList.add(refreshToken);

        return tokenList;
    }

    public KakaoApiMemberResponse getMemberInformation(List<Object> tokenList) {

        String accessToken = (String) tokenList.get(0);
        String refreshToken = (String) tokenList.get(1);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
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
        String alias = body.getJSONObject("properties").getString("nickname");
        String email = body.getJSONObject("kakao_account").getString("email");

        return new KakaoApiMemberResponse(accessToken, refreshToken, alias, email);
    }

}


