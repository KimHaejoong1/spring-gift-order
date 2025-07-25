package gift.controller;

import gift.dto.KakaoTokenResponseDTO;
import gift.service.KakaoAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oauth")
public class KakaoAuthController {
    private final KakaoAuthService kakaoAuthService;

    public KakaoAuthController(KakaoAuthService kakaoAuthService) {
        this.kakaoAuthService = kakaoAuthService;
    }

    @PostMapping("/token")
    public ResponseEntity<KakaoTokenResponseDTO> getToken(@RequestParam String code) {
        var tokenResponse = kakaoAuthService.getAccessToken(code);
        return ResponseEntity.ok(tokenResponse);
    }
}
