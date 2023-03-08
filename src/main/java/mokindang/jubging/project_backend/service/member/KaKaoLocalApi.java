package mokindang.jubging.project_backend.service.member;

import mokindang.jubging.project_backend.service.member.request.RegionUpdateRequest;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Component
public class KaKaoLocalApi {

    public static final String REGION_2_DEPTH_NAME = "region_2depth_name";

    public String switchCoordinateToRegion(RegionUpdateRequest regionUpdateRequest) {
        String url = "https://dapi.kakao.com/v2/local/geo/coord2address.json?x=" + regionUpdateRequest.getLongitude() + "&y=" + regionUpdateRequest.getLatitude();
        String addr = "";
        try {
            addr = getRegionAddress(getJSONData(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return addr;
    }

    private String getJSONData(String apiUrl) throws Exception {
        HttpURLConnection conn = null;
        StringBuilder response = new StringBuilder();

        String auth = "KakaoAK " + "60b35611c843f6c8f618a495ecc8eaf6";

        URL url = new URL(apiUrl);

        conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("X-Requested-With", "curl");
        conn.setRequestProperty("Authorization", auth);
        conn.setDoOutput(true);

        int responseCode = conn.getResponseCode();
        if (responseCode == 400) {
            throw (new IllegalArgumentException("400:: 해당 명령을 실행할 수 없음"));
        } else if (responseCode == 401) {
            throw (new IllegalArgumentException("401:: Authorization가 잘못됨"));
        } else if (responseCode == 500) {
            throw (new IllegalArgumentException("500:: 서버 에러, 문의 필요"));
        } else {
            Charset charset = StandardCharsets.UTF_8;
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));

            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
        }
        return response.toString();
    }

    private String getRegionAddress(String jsonString) {
        String regionDepth2 = "";
        JSONObject jObj = (JSONObject) JSONValue.parse(jsonString);
        JSONObject meta = (JSONObject) jObj.get("meta");
        long size = (long) meta.get("total_count");

        if (size > 0) {
            JSONArray jArray = (JSONArray) jObj.get("documents");
            JSONObject subJobj = (JSONObject) jArray.get(0);
            JSONObject roadAddress = (JSONObject) subJobj.get("road_address");

            if (roadAddress == null) {
                JSONObject subsubJobj = (JSONObject) subJobj.get("address");
                regionDepth2 = (String) subsubJobj.get(REGION_2_DEPTH_NAME);
            } else {
                regionDepth2 = (String) roadAddress.get(REGION_2_DEPTH_NAME);
            }

            if (regionDepth2.equals("") || regionDepth2 == null) {
                subJobj = (JSONObject) jArray.get(1);
                subJobj = (JSONObject) subJobj.get("address");
                regionDepth2 = (String) subJobj.get(REGION_2_DEPTH_NAME);
            }
        }
        return regionDepth2;
    }
}
