package gift.controller;

import gift.config.KakaoProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/kakao")
public class KakaoAuthViewController {
    private final KakaoProperties kakaoProperties;

    public KakaoAuthViewController(KakaoProperties kakaoProperties) {
        this.kakaoProperties = kakaoProperties;
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("loginUrl", kakaoProperties.loginUrl());
        return "kakao-login";
    }
}
