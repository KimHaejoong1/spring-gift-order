package gift.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoTokenResponseDTO(
        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("expires_in")
        String expiresIn,

        @JsonProperty("refresh_token")
        String refreshToken,

        @JsonProperty("refresh_token_expires_in")
        String refreshTokenExpiresIn
) {}
