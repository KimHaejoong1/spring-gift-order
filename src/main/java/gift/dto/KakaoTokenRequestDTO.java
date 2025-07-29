package gift.dto;

import org.springframework.util.LinkedMultiValueMap;

public record KakaoTokenRequestDTO(
        String grantType,
        String clientId,
        String redirectUri,
        String code
) {
    public KakaoTokenRequestDTO(String client_id, String redirectUri, String code) {
        this("authorization_code", client_id, redirectUri, code);
    }

    public LinkedMultiValueMap<String, String> toMultiValueMap() {
        var map = new LinkedMultiValueMap<String, String>();
        map.add("grant_type", grantType);
        map.add("client_id", clientId);
        map.add("redirect_uri", redirectUri);
        map.add("code", code);
        return map;
    }
}
