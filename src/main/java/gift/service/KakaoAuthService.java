package gift.service;

import gift.config.KakaoProperties;
import gift.dto.KakaoTokenRequestDTO;
import gift.dto.KakaoTokenResponseDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;

import java.net.URI;

@Service
public class KakaoAuthService {
    private final RestTemplate restTemplate;
    private final KakaoProperties kakaoProperties;

    public KakaoAuthService(RestTemplate restTemplate, KakaoProperties kakaoProperties) {
        this.restTemplate = restTemplate;
        this.kakaoProperties = kakaoProperties;
    }

    public KakaoTokenResponseDTO getAccessToken(String code) {
        var headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        var requestDTO = new KakaoTokenRequestDTO(
                kakaoProperties.restApiKey(),
                kakaoProperties.redirectUri(),
                code
        );

        var body = requestDTO.toMultiValueMap();

        var request = new RequestEntity<>(body, headers, HttpMethod.POST, URI.create(kakaoProperties.tokenUri()));
        var response = restTemplate.exchange(request, KakaoTokenResponseDTO.class);

        return response.getBody();
    }
}
