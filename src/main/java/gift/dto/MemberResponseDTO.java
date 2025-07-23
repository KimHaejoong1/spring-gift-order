package gift.dto;

import gift.entity.Role;

public record MemberResponseDTO(
        Integer id,
        String email,
        Role role
) {}
