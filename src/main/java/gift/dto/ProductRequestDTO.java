package gift.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigInteger;

public record ProductRequestDTO (
        @NotBlank(message = "상품 이름은 필수 입력 항목입니다.")
        @Size(max = 15, message = "상품 이름은 최대 15자까지 입력할 수 있습니다.")
        @Pattern(
                regexp = "^[\\p{L}\\p{N}\\s()\\[\\]+\\-&/_]*$",
                message = "(),[],+,-,&,/,_ 외의 특수 문자는 사용이 불가합니다."
        )
        @Pattern(
                regexp = "^(?!.*카카오).*$",
                message = "'카카오'가 포함된 상품명은 담당 MD와 협의 후 사용 가능합니다."
        )
        String name,
        @NotNull(message = "상품 가격은 필수 입력 항목입니다.")
        @Positive(message = "상품 가격은 0보다 큰 값이어야 합니다.")
        BigInteger price,
        @NotBlank(message = "이미지 URL은 필수 입력 항목입니다.")
        @Size(max = 1000, message = "이미지 URL은 1000자를 초과할 수 없습니다.")
        String imageUrl
) {}
