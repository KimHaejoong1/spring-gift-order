package gift.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/kakao")
public class KakaoAuthViewController {
    @Value("${kakao.login-url}")
    private String loginUrl;

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("loginUrl", loginUrl);
        return "kakao-login";
    }
}
