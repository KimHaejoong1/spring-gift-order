package gift.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OrderRequestDTO(
        @NotNull(message = "옵션 ID는 필수입니다.")
        Integer optionId,

        @NotNull(message = "수량은 필수입니다.")
        @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
        Integer quantity,

        @Size(max = 100, message = "Message는 100자 이하여야 합니다.")
        String message
) {}
