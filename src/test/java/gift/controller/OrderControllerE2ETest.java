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
public class OrderControllerE2ETest {
    @Autowired
    private TestRestTemplate restTemplate;

    private String authToken;
    private Integer optionId;
    private Integer productId;

    @BeforeEach
    void setUp() {
        MemberRequestDTO memberRequest = new MemberRequestDTO("test@example.com", "password", Role.USER);
        ResponseEntity<AuthTokenResponseDTO> memberResponse = restTemplate.postForEntity("/api/members/register", memberRequest, AuthTokenResponseDTO.class);
        authToken = memberResponse.getBody().token();

        ProductRequestDTO productRequest = new ProductRequestDTO("휠렛버거", BigInteger.valueOf(5000), "https://example.com/image.jpg");
        ResponseEntity<ProductResponseDTO> productResponse = restTemplate.postForEntity("/api/products", productRequest, ProductResponseDTO.class);
        productId = productResponse.getBody().id();

        OptionRequestDTO optionRequest = new OptionRequestDTO("기본옵션", 10);
        ResponseEntity<OptionResponseDTO> optionResponse = restTemplate.postForEntity("/api/products/" + productId + "/options", optionRequest, OptionResponseDTO.class);
        optionId = optionResponse.getBody().id();
    }

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Test
    void createOrder_success() {
        OrderRequestDTO request = new OrderRequestDTO(optionId, 2, "Please handle this order with care.");
        HttpEntity<OrderRequestDTO> httpEntity = new HttpEntity<>(request, createAuthHeaders());

        ResponseEntity<OrderResponseDTO> response = restTemplate.exchange("/api/orders", HttpMethod.POST, httpEntity, OrderResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().optionId()).isEqualTo(optionId);
        assertThat(response.getBody().quantity()).isEqualTo(2);
        assertThat(response.getBody().message()).isEqualTo("Please handle this order with care.");
        assertThat(response.getBody().id()).isNotNull();
        assertThat(response.getBody().orderDateTime()).isNotNull();
    }

    @Test
    void createOrder_validation_failed_null_optionId() {
        OrderRequestDTO request = new OrderRequestDTO(null, 2, "test message");
        HttpEntity<OrderRequestDTO> httpEntity = new HttpEntity<>(request, createAuthHeaders());

        ResponseEntity<String> response = restTemplate.exchange("/api/orders", HttpMethod.POST, httpEntity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("옵션 ID는 필수입니다");
    }

    @Test
    void createOrder_validation_failed_invalid_quantity() {
        OrderRequestDTO request = new OrderRequestDTO(optionId, 0, "test message");
        HttpEntity<OrderRequestDTO> httpEntity = new HttpEntity<>(request, createAuthHeaders());

        ResponseEntity<String> response = restTemplate.exchange("/api/orders", HttpMethod.POST, httpEntity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("수량은 1개 이상이어야 합니다");
    }

    @Test
    void createOrder_unauthorized() {
        OrderRequestDTO request = new OrderRequestDTO(optionId, 2, "test message");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<OrderRequestDTO> httpEntity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange("/api/orders", HttpMethod.POST, httpEntity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void createOrder_success_and_decrease_option_quantity() {
        ResponseEntity<OptionResponseDTO[]> optionsResponse = restTemplate.getForEntity("/api/products/" + productId + "/options", OptionResponseDTO[].class);
        int initialQuantity = optionsResponse.getBody()[0].quantity();
        assertThat(initialQuantity).isEqualTo(10);

        OrderRequestDTO request = new OrderRequestDTO(optionId, 2, "test message");
        HttpEntity<OrderRequestDTO> httpEntity = new HttpEntity<>(request, createAuthHeaders());
        ResponseEntity<OrderResponseDTO> response = restTemplate.exchange("/api/orders", HttpMethod.POST, httpEntity, OrderResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ResponseEntity<OptionResponseDTO[]> updatedOptionsResponse = restTemplate.getForEntity("/api/products/" + productId + "/options", OptionResponseDTO[].class);
        int updatedQuantity = updatedOptionsResponse.getBody()[0].quantity();
        assertThat(updatedQuantity).isEqualTo(8);
    }

    @Test
    void createOrder_failed_insufficient_stock() {
        OrderRequestDTO request = new OrderRequestDTO(optionId, 15, "test message");
        HttpEntity<OrderRequestDTO> httpEntity = new HttpEntity<>(request, createAuthHeaders());

        ResponseEntity<String> response = restTemplate.exchange("/api/orders", HttpMethod.POST, httpEntity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("재고가 부족합니다");
    }

    @Test
    void createOrder_failed_option_not_found() {
        OrderRequestDTO request = new OrderRequestDTO(999, 2, "test message");
        HttpEntity<OrderRequestDTO> httpEntity = new HttpEntity<>(request, createAuthHeaders());

        ResponseEntity<String> response = restTemplate.exchange("/api/orders", HttpMethod.POST, httpEntity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("옵션을 찾을 수 없습니다");
    }

    @Test
    void createOrder_remove_from_wishlist() {
        WishlistRequestDTO wishlistRequest = new WishlistRequestDTO(productId, 1);
        HttpEntity<WishlistRequestDTO> wishlistEntity = new HttpEntity<>(wishlistRequest, createAuthHeaders());
        ResponseEntity<WishlistResponseDTO> wishlistResponse = restTemplate.exchange("/api/wishlist", HttpMethod.POST, wishlistEntity, WishlistResponseDTO.class);

        assertThat(wishlistResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<WishlistResponseDTO[]> beforeWishlist = restTemplate.exchange("/api/wishlist", HttpMethod.GET, new HttpEntity<>(createAuthHeaders()), WishlistResponseDTO[].class);
        assertThat(beforeWishlist.getBody()).hasSize(1);
        assertThat(beforeWishlist.getBody()[0].productId()).isEqualTo(productId);

        OrderRequestDTO orderRequest = new OrderRequestDTO(optionId, 2, "Please remove from wishlist");
        HttpEntity<OrderRequestDTO> orderEntity = new HttpEntity<>(orderRequest, createAuthHeaders());
        ResponseEntity<OrderResponseDTO> orderResponse = restTemplate.exchange("/api/orders", HttpMethod.POST, orderEntity, OrderResponseDTO.class);

        assertThat(orderResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<WishlistResponseDTO[]> afterWishlist = restTemplate.exchange("/api/wishlist", HttpMethod.GET, new HttpEntity<>(createAuthHeaders()), WishlistResponseDTO[].class);
        assertThat(afterWishlist.getBody()).hasSize(0);
    }

    @Test
    void createOrder_no_wishlist_to_remove() {
        ResponseEntity<WishlistResponseDTO[]> beforeWishlist = restTemplate.exchange("/api/wishlist", HttpMethod.GET, new HttpEntity<>(createAuthHeaders()), WishlistResponseDTO[].class);
        assertThat(beforeWishlist.getBody()).hasSize(0);

        OrderRequestDTO orderRequest = new OrderRequestDTO(optionId, 2, "No wishlist item to remove");
        HttpEntity<OrderRequestDTO> orderEntity = new HttpEntity<>(orderRequest, createAuthHeaders());
        ResponseEntity<OrderResponseDTO> orderResponse = restTemplate.exchange("/api/orders", HttpMethod.POST, orderEntity, OrderResponseDTO.class);

        assertThat(orderResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<WishlistResponseDTO[]> afterWishlist = restTemplate.exchange("/api/wishlist", HttpMethod.GET, new HttpEntity<>(createAuthHeaders()), WishlistResponseDTO[].class);
        assertThat(afterWishlist.getBody()).hasSize(0);
    }

    @Test
    void createOrder_remove_only_matching_product_from_wishlist() {
        WishlistRequestDTO wishlistRequest1 = new WishlistRequestDTO(productId, 1);
        HttpEntity<WishlistRequestDTO> wishlistEntity1 = new HttpEntity<>(wishlistRequest1, createAuthHeaders());
        restTemplate.exchange("/api/wishlist", HttpMethod.POST, wishlistEntity1, WishlistResponseDTO.class);

        ProductRequestDTO anotherProductRequest = new ProductRequestDTO("딥치즈버거", BigInteger.valueOf(5500), "https://example.com/other.jpg");
        ResponseEntity<ProductResponseDTO> anotherProductResponse = restTemplate.postForEntity("/api/products", anotherProductRequest, ProductResponseDTO.class);
        Integer anotherProductId = anotherProductResponse.getBody().id();

        WishlistRequestDTO wishlistRequest2 = new WishlistRequestDTO(anotherProductId, 2);
        HttpEntity<WishlistRequestDTO> wishlistEntity2 = new HttpEntity<>(wishlistRequest2, createAuthHeaders());
        restTemplate.exchange("/api/wishlist", HttpMethod.POST, wishlistEntity2, WishlistResponseDTO.class);

        ResponseEntity<WishlistResponseDTO[]> beforeWishlist = restTemplate.exchange("/api/wishlist", HttpMethod.GET, new HttpEntity<>(createAuthHeaders()), WishlistResponseDTO[].class);
        assertThat(beforeWishlist.getBody()).hasSize(2);

        OrderRequestDTO orderRequest = new OrderRequestDTO(optionId, 1, "Remove only first product");
        HttpEntity<OrderRequestDTO> orderEntity = new HttpEntity<>(orderRequest, createAuthHeaders());
        ResponseEntity<OrderResponseDTO> orderResponse = restTemplate.exchange("/api/orders", HttpMethod.POST, orderEntity, OrderResponseDTO.class);

        assertThat(orderResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<WishlistResponseDTO[]> afterWishlist = restTemplate.exchange("/api/wishlist", HttpMethod.GET, new HttpEntity<>(createAuthHeaders()), WishlistResponseDTO[].class);
        assertThat(afterWishlist.getBody()).hasSize(1);
        assertThat(afterWishlist.getBody()[0].productId()).isEqualTo(anotherProductId);
    }
}
