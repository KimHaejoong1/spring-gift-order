package gift.service;

import gift.dto.OptionRequestDTO;
import gift.dto.OptionResponseDTO;
import gift.entity.Option;
import gift.entity.Product;
import gift.repository.OptionRepository;
import gift.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class OptionService {
    private final OptionRepository optionRepository;
    private final ProductRepository productRepository;

    public OptionService(OptionRepository optionRepository, ProductRepository productRepository) {
        this.optionRepository = optionRepository;
        this.productRepository = productRepository;
    }

    public List<OptionResponseDTO> getOptionsByProductId(Integer productId) {
        List<Option> options = optionRepository.findByProductId(productId);
        return options.stream()
                .map(option -> new OptionResponseDTO(
                        option.getId(),
                        option.getName(),
                        option.getQuantity()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public OptionResponseDTO createOption(Integer productId, OptionRequestDTO optionRequestDTO) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        if (product.hasOptionWithName(optionRequestDTO.name())) {
            throw new IllegalArgumentException("동일한 상품 내에 중복된 옵션명이 존재합니다.");
        }

        Option option = new Option(
                optionRequestDTO.name(),
                optionRequestDTO.quantity(),
                product
        );

        Option saved = optionRepository.save(option);

        return new OptionResponseDTO(
                saved.getId(),
                saved.getName(),
                saved.getQuantity()
        );
    }

    @Transactional
    public void subtractQuantity(Integer optionId, Integer quantity) {
        Option option = optionRepository.findById(optionId)
                .orElseThrow(() -> new IllegalArgumentException("옵션을 찾을 수 없습니다."));

        option.subtractQuantity(quantity);
    }
}
