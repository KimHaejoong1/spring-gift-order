package gift.service;

import gift.dto.ProductRequestDTO;
import gift.dto.ProductResponseDTO;
import gift.entity.Product;
import gift.exception.ResourceNotFoundException;
import gift.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductResponseDTO> getAll() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(product -> new ProductResponseDTO(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        product.getImageUrl()
                ))
                .collect(Collectors.toList());
    }

    public Page<ProductResponseDTO> getPage(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(product -> new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImageUrl()
        ));
    }

    public ProductResponseDTO getById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("상품을 찾을 수 없습니다."));

        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImageUrl()
        );
    }

    public Product getEntityById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("상품을 찾을 수 없습니다."));
    }

    @Transactional
    public ProductResponseDTO create(ProductRequestDTO productRequestDTO) {
        Product product = new Product(
                productRequestDTO.name(),
                productRequestDTO.price(),
                productRequestDTO.imageUrl()
        );

        Product saved = productRepository.save(product);

        return new ProductResponseDTO(
                saved.getId(),
                saved.getName(),
                saved.getPrice(),
                saved.getImageUrl()
        );
    }

    @Transactional
    public ProductResponseDTO update(Integer id, ProductRequestDTO productRequestDTO) {
        if(!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("상품을 찾을 수 없습니다.");
        }

        Product product = new Product(
                id,
                productRequestDTO.name(),
                productRequestDTO.price(),
                productRequestDTO.imageUrl()
        );
        Product updated = productRepository.save(product);

        return new ProductResponseDTO(
                updated.getId(),
                updated.getName(),
                updated.getPrice(),
                updated.getImageUrl()
        );
    }

    @Transactional
    public void delete(Integer id) {
        if(!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("상품을 찾을 수 없습니다.");
        }
        productRepository.deleteById(id);
    }
}
