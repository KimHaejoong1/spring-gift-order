package gift.controller;

import gift.dto.AuthTokenResponseDTO;
import gift.dto.MemberLoginRequestDTO;
import gift.dto.MemberRequestDTO;
import gift.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthTokenResponseDTO> register(@Valid @RequestBody MemberRequestDTO member) {
        AuthTokenResponseDTO token = memberService.register(member);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(token);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthTokenResponseDTO> login(@Valid @RequestBody MemberLoginRequestDTO member) {
        AuthTokenResponseDTO token = memberService.login(member);
        return ResponseEntity.ok(token);
    }
}
