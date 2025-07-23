package gift.dto;

import java.math.BigInteger;

public record WishlistResponseDTO (
        Integer id,
        Integer productId,
        String productName,
        BigInteger productPrice,
        String productImageUrl,
        Integer quantity
) {}
