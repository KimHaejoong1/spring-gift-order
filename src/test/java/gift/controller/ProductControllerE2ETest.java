package gift.controller;

import gift.dto.ProductRequestDTO;
import gift.dto.ProductResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ProductControllerE2ETest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createProduct_success() {
        ProductRequestDTO request = new ProductRequestDTO("테스트 상품", BigInteger.valueOf(10000), "http://example.com/image.jpg");

        ResponseEntity<ProductResponseDTO> response = restTemplate.postForEntity("/api/products", request, ProductResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().name()).isEqualTo("테스트 상품");
        assertThat(response.getBody().price()).isEqualTo(BigInteger.valueOf(10000));
        assertThat(response.getBody().imageUrl()).isEqualTo("http://example.com/image.jpg");
    }

    @Test
    void createProduct_validation_failed_long_name() {
        ProductRequestDTO request = new ProductRequestDTO("휠렛버거휠렛버거휠렛버거휠렛버거", BigInteger.valueOf(5000), "http://example.com");

        ResponseEntity<String> response = restTemplate.postForEntity("/api/products", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("상품 이름은 최대 15자까지 입력할 수 있습니다.");
    }

    @Test
    void createProduct_validation_failed_kakao() {
        ProductRequestDTO request = new ProductRequestDTO("카카오 상품", BigInteger.valueOf(1000), "http://example.com");

        ResponseEntity<String> response = restTemplate.postForEntity("/api/products", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("'카카오'가 포함된 상품명은 담당 MD와 협의 후 사용 가능합니다.");
    }

    @Test
    void createProduct_validation_failed_specialCharacter() {
        ProductRequestDTO request = new ProductRequestDTO("상품@#$", BigInteger.valueOf(1000), "http://example.com");

        ResponseEntity<String> response = restTemplate.postForEntity("/api/products", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("(),[],+,-,&,/,_ 외의 특수 문자는 사용이 불가합니다.");
    }

    @Test
    void getAllProducts_success() {
        ProductRequestDTO product1 = new ProductRequestDTO("상품1", BigInteger.valueOf(1000), "http://example.com/1.jpg");
        ProductRequestDTO product2 = new ProductRequestDTO("상품2", BigInteger.valueOf(2000), "http://example.com/2.jpg");

        restTemplate.postForEntity("/api/products", product1, ProductResponseDTO.class);
        restTemplate.postForEntity("/api/products", product2, ProductResponseDTO.class);

        ResponseEntity<ProductResponseDTO[]> response = restTemplate.getForEntity("/api/products", ProductResponseDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void getProductById_success() {
        ProductRequestDTO request = new ProductRequestDTO("조회용 상품", BigInteger.valueOf(5000), "http://example.com/test.jpg");
        restTemplate.postForEntity("/api/products", request, ProductResponseDTO.class);

        ResponseEntity<ProductResponseDTO> response = restTemplate.getForEntity("/api/products/" + "1", ProductResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().name()).isEqualTo("조회용 상품");
    }

    @Test
    void getProductById_failed() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/products/999", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateProduct_success() {
        ProductRequestDTO createRequest = new ProductRequestDTO("수정 전", BigInteger.valueOf(1000), "http://before.com");
        ResponseEntity<ProductResponseDTO> createResponse = restTemplate.postForEntity("/api/products", createRequest, ProductResponseDTO.class);

        ProductRequestDTO updateRequest = new ProductRequestDTO("수정 후", BigInteger.valueOf(2000), "http://after.com");
        HttpEntity<ProductRequestDTO> httpEntity = new HttpEntity<>(updateRequest);
        ResponseEntity<ProductResponseDTO> response = restTemplate.exchange("/api/products/1", HttpMethod.PUT, httpEntity, ProductResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().name()).isEqualTo("수정 후");
        assertThat(response.getBody().price()).isEqualTo(BigInteger.valueOf(2000));
        assertThat(response.getBody().imageUrl()).isEqualTo("http://after.com");
    }

    @Test
    void updateProduct_validation_failed_longName() {
        ProductRequestDTO createRequest = new ProductRequestDTO("수정 전", BigInteger.valueOf(1000), "http://before.com");
        restTemplate.postForEntity("/api/products", createRequest, ProductResponseDTO.class);

        ProductRequestDTO updateRequest = new ProductRequestDTO("휠렛버거휠렛버거휠렛버거휠렛버거", BigInteger.valueOf(2000), "http://after.com");
        HttpEntity<ProductRequestDTO> httpEntity = new HttpEntity<>(updateRequest);
        ResponseEntity<String> response = restTemplate.exchange("/api/products/1", HttpMethod.PUT, httpEntity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("상품 이름은 최대 15자까지 입력할 수 있습니다.");
    }

    @Test
    void updateProduct_validation_failed_kakao() {
        ProductRequestDTO createRequest = new ProductRequestDTO("수정 전", BigInteger.valueOf(1000), "http://before.com");
        restTemplate.postForEntity("/api/products", createRequest, ProductResponseDTO.class);

        ProductRequestDTO updateRequest = new ProductRequestDTO("카카오 상품", BigInteger.valueOf(2000), "http://after.com");
        HttpEntity<ProductRequestDTO> httpEntity = new HttpEntity<>(updateRequest);
        ResponseEntity<String> response = restTemplate.exchange("/api/products/1", HttpMethod.PUT, httpEntity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("'카카오'가 포함된 상품명은 담당 MD와 협의 후 사용 가능합니다.");
    }

    @Test
    void updateProduct_validation_failed_specialCharacter() {
        ProductRequestDTO createRequest = new ProductRequestDTO("수정 전", BigInteger.valueOf(1000), "http://before.com");
        restTemplate.postForEntity("/api/products", createRequest, ProductResponseDTO.class);

        ProductRequestDTO updateRequest = new ProductRequestDTO("상품@#$", BigInteger.valueOf(2000), "http://after.com");
        HttpEntity<ProductRequestDTO> httpEntity = new HttpEntity<>(updateRequest);
        ResponseEntity<String> response = restTemplate.exchange("/api/products/1", HttpMethod.PUT, httpEntity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("(),[],+,-,&,/,_ 외의 특수 문자는 사용이 불가합니다.");
    }

    @Test
    void validation_test_simple() {
        // null 값으로 테스트 (기본 @NotNull 검증)
        ProductRequestDTO request = new ProductRequestDTO("휠렛버거휠렛버거휠렛버거휠렛버거", BigInteger.valueOf(1000), "http://example.com");

        ResponseEntity<String> response = restTemplate.postForEntity("/api/products", request, String.class);


        // @Valid가 정상 작동하면 400이어야 함
        System.out.println("Status: " + response.getStatusCode());
        System.out.println("Body: " + response.getBody());
    }

    @Test
    void deleteProduct_success() {
        ProductRequestDTO request = new ProductRequestDTO("삭제용 상품", BigInteger.valueOf(1000), "http://example.com");
        restTemplate.postForEntity("/api/products", request, ProductResponseDTO.class);

        ResponseEntity<Void> response = restTemplate.exchange("/api/products/1", HttpMethod.DELETE, null, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void getProductsByPage_first_page() {
        for (int i = 1; i <= 15; i++) {
            ProductRequestDTO request = new ProductRequestDTO(
                    "상품" + i,
                    BigInteger.valueOf(1000 * i),
                    "http://example.com/image" + i + ".jpg"
            );
            restTemplate.postForEntity("/api/products", request, ProductResponseDTO.class);
        }

        String url = "/api/products/page?page=0&size=10&sort=id";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"totalElements\":15");
        assertThat(response.getBody()).contains("\"totalPages\":2");
        assertThat(response.getBody()).contains("\"first\":true");
        assertThat(response.getBody()).contains("\"last\":false");
        assertThat(response.getBody()).contains("\"size\":10");
    }

    @Test
    void getProductsByPage_last_page() {
        for (int i = 1; i <= 15; i++) {
            ProductRequestDTO request = new ProductRequestDTO(
                    "상품" + i,
                    BigInteger.valueOf(1000 * i),
                    "http://example.com/image" + i + ".jpg"
            );
            restTemplate.postForEntity("/api/products", request, ProductResponseDTO.class);
        }

        String url = "/api/products/page?page=1&size=10&sort=id";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"totalElements\":15");
        assertThat(response.getBody()).contains("\"totalPages\":2");
        assertThat(response.getBody()).contains("\"first\":false");
        assertThat(response.getBody()).contains("\"last\":true");
        assertThat(response.getBody()).contains("\"numberOfElements\":5");
    }

    @Test
    void getProductsByPage_empty_page() {
        for (int i = 1; i <= 5; i++) {
            ProductRequestDTO request = new ProductRequestDTO(
                    "상품" + i,
                    BigInteger.valueOf(1000 * i),
                    "http://example.com/image" + i + ".jpg"
            );
            restTemplate.postForEntity("/api/products", request, ProductResponseDTO.class);
        }

        String url = "/api/products/page?page=2&size=10";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"content\":[]"); // 빈 배열
        assertThat(response.getBody()).contains("\"totalElements\":5");
        assertThat(response.getBody()).contains("\"totalPages\":1");
    }
}
