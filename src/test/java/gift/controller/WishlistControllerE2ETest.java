package gift.controller;

import gift.dto.*;
import gift.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class WishlistControllerE2ETest {
    @Autowired
    private TestRestTemplate restTemplate;

    private String authToken;
    private Integer productId;

    @BeforeEach
    void setUp() {
        MemberRequestDTO memberRequest = new MemberRequestDTO("test@example.com", "password", Role.USER);
        ResponseEntity<AuthTokenResponseDTO> memberResponse = restTemplate.postForEntity("/api/members/register", memberRequest, AuthTokenResponseDTO.class);
        authToken = memberResponse.getBody().token();

        ProductRequestDTO productRequest = new ProductRequestDTO("휠렛버거", BigInteger.valueOf(5000), "https://example.com/image.jpg");
        ResponseEntity<ProductResponseDTO> productResponse = restTemplate.postForEntity("/api/products", productRequest, ProductResponseDTO.class);
        productId = productResponse.getBody().id();
    }

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Test
    void addWishlist_success() {
        WishlistRequestDTO request = new WishlistRequestDTO(productId, 2);
        HttpEntity<WishlistRequestDTO> httpEntity = new HttpEntity<>(request, createAuthHeaders());

        ResponseEntity<WishlistResponseDTO> response = restTemplate.exchange("/api/wishlist", HttpMethod.POST, httpEntity, WishlistResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().productId()).isEqualTo(productId);
        assertThat(response.getBody().quantity()).isEqualTo(2);
    }

    @Test
    void getWishlist_success() {
        WishlistRequestDTO request = new WishlistRequestDTO(productId, 3);
        HttpEntity<WishlistRequestDTO> addEntity = new HttpEntity<>(request, createAuthHeaders());
        restTemplate.exchange("/api/wishlist", HttpMethod.POST, addEntity, WishlistResponseDTO.class);

        HttpEntity<Void> getEntity = new HttpEntity<>(createAuthHeaders());
        ResponseEntity<WishlistResponseDTO[]> response = restTemplate.exchange("/api/wishlist", HttpMethod.GET, getEntity, WishlistResponseDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody()[0].quantity()).isEqualTo(3);
    }

    @Test
    void updateWishlist_success() {
        WishlistRequestDTO request = new WishlistRequestDTO(productId, 2);
        HttpEntity<WishlistRequestDTO> addEntity = new HttpEntity<>(request, createAuthHeaders());
        ResponseEntity<WishlistResponseDTO> addResponse = restTemplate.exchange("/api/wishlist", HttpMethod.POST, addEntity, WishlistResponseDTO.class);
        Integer wishlistId = addResponse.getBody().id();

        WishlistUpdateRequestDTO updateRequest = new WishlistUpdateRequestDTO(5);
        HttpEntity<WishlistUpdateRequestDTO> updateEntity = new HttpEntity<>(updateRequest, createAuthHeaders());
        ResponseEntity<WishlistResponseDTO> response = restTemplate.exchange("/api/wishlist/" + wishlistId, HttpMethod.PUT, updateEntity, WishlistResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().quantity()).isEqualTo(5);
    }

    @Test
    void deleteWishlist_success() {
        WishlistRequestDTO request = new WishlistRequestDTO(productId, 2);
        HttpEntity<WishlistRequestDTO> addEntity = new HttpEntity<>(request, createAuthHeaders());
        ResponseEntity<WishlistResponseDTO> addResponse = restTemplate.exchange("/api/wishlist", HttpMethod.POST, addEntity, WishlistResponseDTO.class);
        Integer wishlistId = addResponse.getBody().id();

        HttpEntity<Void> deleteEntity = new HttpEntity<>(createAuthHeaders());
        ResponseEntity<Void> response = restTemplate.exchange("/api/wishlist/" + wishlistId, HttpMethod.DELETE, deleteEntity, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void addWishlist_validation_failed() {
        WishlistRequestDTO request = new WishlistRequestDTO(productId, 0); // 수량이 0 (validation 실패)
        HttpEntity<WishlistRequestDTO> httpEntity = new HttpEntity<>(request, createAuthHeaders());

        ResponseEntity<String> response = restTemplate.exchange("/api/wishlist", HttpMethod.POST, httpEntity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("수량은 1개 이상이어야 합니다.");
    }

    @Test
    void addWishlist_unauthorized() {
        WishlistRequestDTO request = new WishlistRequestDTO(productId, 2);
        HttpEntity<WishlistRequestDTO> httpEntity = new HttpEntity<>(request, new HttpHeaders()); // 인증 헤더 없음

        ResponseEntity<String> response = restTemplate.exchange("/api/wishlist", HttpMethod.POST, httpEntity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void getWishlistsByPage_first_page() {
        for (int i = 1; i <= 12; i++) {
            ProductRequestDTO productRequest = new ProductRequestDTO("상품" + i, BigInteger.valueOf(1000 * i), "https://example.com/" + i + ".jpg");
            ResponseEntity<ProductResponseDTO> productResponse = restTemplate.postForEntity("/api/products", productRequest, ProductResponseDTO.class);
            Integer newProductId = productResponse.getBody().id();

            WishlistRequestDTO wishlistRequest = new WishlistRequestDTO(newProductId, i);
            HttpEntity<WishlistRequestDTO> httpEntity = new HttpEntity<>(wishlistRequest, createAuthHeaders());
            restTemplate.exchange("/api/wishlist", HttpMethod.POST, httpEntity, WishlistResponseDTO.class);
        }

        String url = "/api/wishlist/page?page=0&size=5&sort=id";
        HttpEntity<Void> httpEntity = new HttpEntity<>(createAuthHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"totalElements\":12");
        assertThat(response.getBody()).contains("\"totalPages\":3");
        assertThat(response.getBody()).contains("\"first\":true");
        assertThat(response.getBody()).contains("\"last\":false");
        assertThat(response.getBody()).contains("\"size\":5");
    }

    @Test
    void getWishlistsByPage_last_page() {
        for (int i = 1; i <= 12; i++) {
            ProductRequestDTO productRequest = new ProductRequestDTO("상품" + i, BigInteger.valueOf(1000 * i), "https://example.com/" + i + ".jpg");
            ResponseEntity<ProductResponseDTO> productResponse = restTemplate.postForEntity("/api/products", productRequest, ProductResponseDTO.class);
            Integer newProductId = productResponse.getBody().id();

            WishlistRequestDTO wishlistRequest = new WishlistRequestDTO(newProductId, i);
            HttpEntity<WishlistRequestDTO> httpEntity = new HttpEntity<>(wishlistRequest, createAuthHeaders());
            restTemplate.exchange("/api/wishlist", HttpMethod.POST, httpEntity, WishlistResponseDTO.class);
        }

        String url = "/api/wishlist/page?page=2&size=5&sort=id";
        HttpEntity<Void> httpEntity = new HttpEntity<>(createAuthHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"totalElements\":12");
        assertThat(response.getBody()).contains("\"totalPages\":3");
        assertThat(response.getBody()).contains("\"first\":false");
        assertThat(response.getBody()).contains("\"last\":true");
        assertThat(response.getBody()).contains("\"numberOfElements\":2");
    }

    @Test
    void getWishlistsByPage_empty_page() {
        for (int i = 1; i <= 3; i++) {
            ProductRequestDTO productRequest = new ProductRequestDTO("상품" + i, BigInteger.valueOf(1000 * i), "https://example.com/" + i + ".jpg");
            ResponseEntity<ProductResponseDTO> productResponse = restTemplate.postForEntity("/api/products", productRequest, ProductResponseDTO.class);
            Integer newProductId = productResponse.getBody().id();

            WishlistRequestDTO wishlistRequest = new WishlistRequestDTO(newProductId, i);
            HttpEntity<WishlistRequestDTO> httpEntity = new HttpEntity<>(wishlistRequest, createAuthHeaders());
            restTemplate.exchange("/api/wishlist", HttpMethod.POST, httpEntity, WishlistResponseDTO.class);
        }

        String url = "/api/wishlist/page?page=1&size=5";
        HttpEntity<Void> httpEntity = new HttpEntity<>(createAuthHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"content\":[]");
        assertThat(response.getBody()).contains("\"totalElements\":3");
        assertThat(response.getBody()).contains("\"totalPages\":1");
    }
}
