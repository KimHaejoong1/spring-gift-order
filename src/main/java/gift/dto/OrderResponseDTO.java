package gift.dto;

import java.time.LocalDateTime;

public record OrderResponseDTO(
        Integer id,
        Integer optionId,
        Integer quantity,
        LocalDateTime orderDateTime,
        String message
) {}
