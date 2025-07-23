package gift.controller;

import gift.dto.AuthTokenResponseDTO;
import gift.dto.MemberRequestDTO;
import gift.dto.MemberResponseDTO;
import gift.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/members")
public class AdminMemberController {
    private final MemberService memberService;

    public AdminMemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public ResponseEntity<List<MemberResponseDTO>> getAllMembers() {
        List<MemberResponseDTO> members = memberService.getAllMembers();
        return ResponseEntity.ok(members);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponseDTO> getMemberById(@PathVariable int id) {
        MemberResponseDTO member = memberService.getMemberById(id);
        return ResponseEntity.ok(member);
    }

    @PostMapping
    public ResponseEntity<AuthTokenResponseDTO> createMember(@RequestBody MemberRequestDTO memberRequestDTO) {
        AuthTokenResponseDTO created = memberService.register(memberRequestDTO);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MemberResponseDTO> updateMember(@PathVariable int id, @RequestBody MemberRequestDTO memberRequestDTO) {
        MemberResponseDTO updated = memberService.update(id, memberRequestDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MemberResponseDTO> deleteMember(@PathVariable int id) {
        memberService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
