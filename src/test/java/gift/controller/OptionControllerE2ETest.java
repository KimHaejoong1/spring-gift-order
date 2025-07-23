package gift.controller;

import gift.dto.OptionRequestDTO;
import gift.dto.OptionResponseDTO;
import gift.dto.ProductRequestDTO;
import gift.dto.ProductResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class OptionControllerE2ETest {
    @Autowired
    private TestRestTemplate restTemplate;

    private Integer productId;

    @BeforeEach
    void setUp() {
        ProductRequestDTO request = new ProductRequestDTO("휠렛버거", BigInteger.valueOf(5000),"https://example.com/image.jpg");
        ResponseEntity<ProductResponseDTO> response = restTemplate.postForEntity("/api/products", request, ProductResponseDTO.class);
        productId = response.getBody().id();
    }

    @Test
    void getOptions_success() {
        OptionRequestDTO option1 = new OptionRequestDTO("기본옵션", 100);
        OptionRequestDTO option2 = new OptionRequestDTO("추가옵션", 50);
        restTemplate.postForEntity("/api/products/" + productId + "/options", option1, OptionResponseDTO.class);
        restTemplate.postForEntity("/api/products/" + productId + "/options", option2, OptionResponseDTO.class);

        ResponseEntity<OptionResponseDTO[]> response = restTemplate.getForEntity("/api/products/" + productId + "/options", OptionResponseDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).extracting("name").containsExactlyInAnyOrder("기본옵션", "추가옵션");
    }

    @Test
    void createOption_success() {
        OptionRequestDTO request = new OptionRequestDTO("성공옵션", 100);

        ResponseEntity<OptionResponseDTO> response = restTemplate.postForEntity("/api/products/" + productId + "/options", request, OptionResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().name()).isEqualTo("성공옵션");
        assertThat(response.getBody().quantity()).isEqualTo(100);
    }

    @Test
    void createOption_failure_invalid_special_characters() {
        OptionRequestDTO request = new OptionRequestDTO("옵션@#$", 100);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/products/" + productId + "/options", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("( ), [ ], +, -, &, /, _ 외의 특수 문자는 사용이 불가합니다.");
    }

    @Test
    void createOption_failure_zero_quantity() {
        OptionRequestDTO request = new OptionRequestDTO("제로옵션", 0);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/products/" + productId + "/options", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("수량은 1개 이상이어야 합니다.");
    }

    @Test
    void createOption_failure_100M_quantity() {
        OptionRequestDTO request = new OptionRequestDTO("1억옵션", 100_000_000);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/products/" + productId + "/options", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("수량은 1억 개 미만이어야 합니다.");
    }

    @Test
    void createOption_failure_duplicate_name() {
        OptionRequestDTO firstOption = new OptionRequestDTO("중복옵션", 100);
        restTemplate.postForEntity("/api/products/" + productId + "/options", firstOption, OptionResponseDTO.class);

        OptionRequestDTO duplicateOption = new OptionRequestDTO("중복옵션", 200);
        ResponseEntity<String> response = restTemplate.postForEntity("/api/products/" + productId + "/options", duplicateOption, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("동일한 상품 내에 중복된 옵션명이 존재합니다.");
    }
}
