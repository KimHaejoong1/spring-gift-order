package gift.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kakao")
public record KakaoProperties(
        String restApiKey,
        String redirectUri,
        String tokenUri,
        String loginUrl
) {}
