package gift.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.config.KakaoProperties;
import gift.dto.KakaoMessageRequestDTO;
import gift.entity.Option;
import gift.entity.Order;
import gift.entity.Product;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Service
public class KakaoMessageService {
    private final RestTemplate restTemplate;
    private final KakaoProperties kakaoProperties;
    private final ObjectMapper objectMapper;

    public KakaoMessageService(RestTemplate restTemplate, KakaoProperties kakaoProperties, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.kakaoProperties = kakaoProperties;
        this.objectMapper = objectMapper;
    }

    public void sendOrderMessage(Order order, Product product, Option option, String accessToken) {
        var headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        String templateObject = createOrderMessageTemplate(order, product, option);

        var requestDto = new KakaoMessageRequestDTO(templateObject);
        var body = requestDto.toMultiValueMap();

        var request = new RequestEntity<>(body, headers, HttpMethod.POST, URI.create(kakaoProperties.messageApiUrl()));
        
        restTemplate.exchange(request, void.class);
    }

    private String createOrderMessageTemplate(Order order, Product product, Option option) {
        try {
            String description = String.format(
                "#%d 상품: %s / %s - %d개\n메세지: %s",
                order.getId(),
                product.getName(),
                option.getName(),
                order.getQuantity(),
                order.getMessage() != null ? order.getMessage() : " "
            );

            Map<String, Object> template = new HashMap<>();
            template.put("object_type", "feed");

            Map<String, Object> content = new HashMap<>();
            content.put("title", "선물을 보냈어요");
            content.put("description", description);
            content.put("image_url", product.getImageUrl());
            content.put("image_width", 640);
            content.put("image_height", 640);

            Map<String, String> link = new HashMap<>();
            link.put("web_url", "http://localhost:8080");
            link.put("mobile_web_url", "http://localhost:8080");
            content.put("link", link);

            template.put("content", content);

            return objectMapper.writeValueAsString(template);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 생성 실패", e);
        }
    }
}
