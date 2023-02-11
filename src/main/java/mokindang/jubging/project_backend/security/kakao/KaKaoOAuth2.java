package mokindang.jubging.project_backend.security.kakao;

import mokindang.jubging.project_backend.domain.member.MemberDto;
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

    public MemberDto getMemberDto(String authorizationCode) {
        //인가코드 -> 액세스 토큰, 리프레쉬 토큰
        List<Object> tokens = getToken(authorizationCode);

        //엑세스 코드로 회원 정보 가져오기
        return getMemberDto(tokens);
    }

    public List<Object> getToken(String authorizationCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HttpBody 오브젝트 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "60b35611c843f6c8f618a495ecc8eaf6");
        params.add("redirect_uri", "http://localhost:8080/api/member/join");
        params.add("code", authorizationCode);

        // HttpHeader와 HttpBody를 하나의 오브젝트에 담기
        RestTemplate rt = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(params, headers);

        // Http 요청하기 - Post방식으로 - 그리고 response 변수의 응답 받음.
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // JSON -> 액세스 토큰 파싱
        String tokenJson = response.getBody();
        JSONObject rjson = new JSONObject(tokenJson);
        String accessToken = rjson.getString("access_token");
        String refreshToken = rjson.getString("refresh_token");

        List<Object> tokenList = new ArrayList<>();
        tokenList.add(accessToken);
        tokenList.add(refreshToken);

        System.out.println("access token:" + accessToken);
        System.out.println("refresh token:" + refreshToken);

        return tokenList;
    }

    public MemberDto getMemberDto(List<Object> tokenList) {

        String accessToken = (String) tokenList.get(0);
        String refreshToken = (String) tokenList.get(1);

        // HttpHeader 오브젝트 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HttpHeader와 HttpBody를 하나의 오브젝트에 담기
        RestTemplate rt = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);

        // Http 요청하기 - Post방식으로 - 그리고 response 변수의 응답 받음.
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        JSONObject body = new JSONObject(response.getBody());
        String alias = body.getJSONObject("properties").getString("nickname");
        String email = body.getJSONObject("kakao_account").getString("email");

        return new MemberDto(accessToken, refreshToken, alias, email);
    }

}


