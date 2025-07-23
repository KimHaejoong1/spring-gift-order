package gift.dto;

import jakarta.validation.constraints.NotBlank;

public record MemberLoginRequestDTO(
        @NotBlank(message = "Email은 필수 입력 항목입니다.")
        String email,
        @NotBlank(message = "Password는 필수 입력 항목입니다.")
        String password
) {}