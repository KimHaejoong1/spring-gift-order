package gift.dto;

public record KakaoTokenRequestDTO(
        String grantType,
        String clientId,
        String redirectUri,
        String code
) {
    public KakaoTokenRequestDTO(String client_id, String redirectUri, String code) {
        this("authorization_code", client_id, redirectUri, code);
    }
}
