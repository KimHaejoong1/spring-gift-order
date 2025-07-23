package gift.dto;

import gift.entity.Role;

public record AuthenticatedMemberDTO(
        Integer id,
        String email,
        Role role
) {}
