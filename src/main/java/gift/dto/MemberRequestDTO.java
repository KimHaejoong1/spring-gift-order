package gift.dto;

import gift.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MemberRequestDTO(
        @NotBlank(message = "Email은 필수 입력 항목입니다.")
        String email,
        @NotBlank(message = "Password는 필수 입력 항목입니다.")
        String password,
        @NotNull
        Role role
) {}
