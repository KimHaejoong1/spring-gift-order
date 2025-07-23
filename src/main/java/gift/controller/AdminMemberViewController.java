package gift.controller;

import gift.dto.MemberRequestDTO;
import gift.dto.MemberResponseDTO;
import gift.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/members")
public class AdminMemberViewController {
    private final MemberService memberService;

    public AdminMemberViewController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public String getAllMembers(Model model) {
        List<MemberResponseDTO> members = memberService.getAllMembers();
        model.addAttribute("members", members);
        return "members";
    }

    @PostMapping("/create")
    public String createMember(@Valid @ModelAttribute MemberRequestDTO member,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            bindingResult.getFieldErrors().forEach(e ->
                    errorMessage.append(e.getDefaultMessage())
                            .append("\n"));
            redirectAttributes.addFlashAttribute("createError", errorMessage.toString());
            redirectAttributes.addFlashAttribute("showCreateModal", true);
            redirectAttributes.addFlashAttribute("createFormData", member);
            return "redirect:/admin/members";
        }

        try {
            memberService.register(member);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("createError", e.getMessage());
            redirectAttributes.addFlashAttribute("showCreateModal", true);
            redirectAttributes.addFlashAttribute("createFormData", member);
            return "redirect:/admin/members";
        }
        return "redirect:/admin/members";
    }

    @PostMapping("/update")
    public String updateMember(
            @RequestParam("id") Integer id,
            @Valid @ModelAttribute MemberRequestDTO member,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            bindingResult.getFieldErrors().forEach(e ->
                    errorMessage.append(e.getDefaultMessage())
                            .append("\n"));
            redirectAttributes.addFlashAttribute("updateError", errorMessage.toString());
            redirectAttributes.addFlashAttribute("showUpdateModal", true);
            redirectAttributes.addFlashAttribute("updateFormData", member);
            redirectAttributes.addFlashAttribute("updateId", id);
            return "redirect:/admin/members";
        }
        try {
            memberService.update(id, member);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("updateError", e.getMessage());
            redirectAttributes.addFlashAttribute("showUpdateModal", true);
            redirectAttributes.addFlashAttribute("updateFormData", member);
            redirectAttributes.addFlashAttribute("updateId", id);
            return "redirect:/admin/members";
        }
        return "redirect:/admin/members";
    }

    @PostMapping("/delete")
    public String deleteMember(@RequestParam("id") Integer id) {
        memberService.delete(id);
        return "redirect:/admin/members";
    }
}
