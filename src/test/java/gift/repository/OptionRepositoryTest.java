package gift.repository;

import gift.entity.Option;
import gift.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigInteger;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class OptionRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OptionRepository optionRepository;

    @Test
    void save_success() {
        Product product = new Product("휠렛버거", BigInteger.valueOf(5000), "https://example.com/image.jpg");
        Product savedProduct = entityManager.persistAndFlush(product);
        Option option = new Option("기본옵션", 100, savedProduct);

        Option saved = optionRepository.save(option);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("기본옵션");
        assertThat(saved.getQuantity()).isEqualTo(100);
        assertThat(saved.getProduct().getId()).isEqualTo(savedProduct.getId());
    }

    @Test
    void findByProductId_success() {
        Product product = new Product("휠렛버거", BigInteger.valueOf(5000), "https://example.com/image.jpg");
        Product savedProduct = entityManager.persistAndFlush(product);
        Option option1 = new Option("옵션1", 100, savedProduct);
        Option option2 = new Option("옵션2", 200, savedProduct);
        entityManager.persistAndFlush(option1);
        entityManager.persistAndFlush(option2);

        List<Option> options = optionRepository.findByProductId(savedProduct.getId());

        assertThat(options).hasSize(2);
        assertThat(options).extracting("name").containsExactlyInAnyOrder("옵션1", "옵션2");
        assertThat(options).extracting("quantity").containsExactlyInAnyOrder(100, 200);
    }

    @Test
    void findByProductId_empty() {
        Product product = new Product("휠렛버거", BigInteger.valueOf(5000), "https://example.com/image.jpg");
        Product savedProduct = entityManager.persistAndFlush(product);

        List<Option> options = optionRepository.findByProductId(savedProduct.getId());

        assertThat(options).isEmpty();
    }
}
