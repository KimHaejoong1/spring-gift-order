package gift.dto;

import jakarta.validation.constraints.*;

public record OptionRequestDTO(
        @NotBlank(message = "옵션명은 필수입니다.")
        @Size(max = 50, message = "옵션명은 최대 50자 까지 입력할 수 있습니다.")
        @Pattern(regexp = "^[\\p{L}\\p{N}\\s()\\[\\]+\\-&/_]*$", message = "( ), [ ], +, -, &, /, _ 외의 특수 문자는 사용이 불가합니다.")
        String name,
        @NotNull(message = "수량은 필수입니다.")
        @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
        @Max(value = 99_999_999, message = "수량은 1억 개 미만이어야 합니다.")
        Integer quantity
) {}
