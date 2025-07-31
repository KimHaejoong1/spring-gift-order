package gift.dto;

import org.springframework.util.LinkedMultiValueMap;

public record KakaoMessageRequestDTO(String templateObject) {
    public LinkedMultiValueMap<String, String> toMultiValueMap() {
        var map = new LinkedMultiValueMap<String, String>();
        map.add("template_object", templateObject);
        return map;
    }
}
