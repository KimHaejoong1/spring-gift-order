package gift.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.config.KakaoProperties;
import gift.dto.KakaoMessageRequestDTO;
import gift.entity.Option;
import gift.entity.Order;
import gift.entity.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(KakaoMessageService.class);

    private final RestTemplate restTemplate;
    private final KakaoProperties kakaoProperties;
    private final ObjectMapper objectMapper;

    public KakaoMessageService(RestTemplate restTemplate, KakaoProperties kakaoProperties, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.kakaoProperties = kakaoProperties;
        this.objectMapper = objectMapper;
    }

    public void sendOrderMessage(Order order, Product product, Option option, String accessToken) {
        String templateObject = createOrderMessageTemplate(order, product, option);
        if (templateObject == null) {
            logger.error("카카오톡 전송 실패");
        }

        var headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        var requestDto = new KakaoMessageRequestDTO(templateObject);
        var body = requestDto.toMultiValueMap();

        var request = new RequestEntity<>(body, headers, HttpMethod.POST, URI.create(kakaoProperties.messageApiUrl()));

        try {
            restTemplate.exchange(request, void.class);
        } catch (Exception e) {
            logger.error("카카오톡 전송 실패", e);
        }

    }

    private String createOrderMessageTemplate(Order order, Product product, Option option) {
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

        try {
            return objectMapper.writeValueAsString(template);
        } catch (JsonProcessingException e) {
            logger.error("JSON 템플릿 생성 실패", e);
            return null;
        }
    }
}
