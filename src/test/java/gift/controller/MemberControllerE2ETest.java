package gift.controller;

import gift.dto.AuthTokenResponseDTO;
import gift.dto.MemberLoginRequestDTO;
import gift.dto.MemberRequestDTO;
import gift.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class MemberControllerE2ETest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void register_success() {
        MemberRequestDTO request = new MemberRequestDTO("test@example.com", "password", Role.USER);

        ResponseEntity<AuthTokenResponseDTO> response = restTemplate.postForEntity("/api/members/register", request, AuthTokenResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().token()).isNotNull();
        assertThat(response.getBody().token()).isNotEmpty();
    }

    @Test
    void register_failed() {
        MemberRequestDTO request = new MemberRequestDTO("test@example.com", "password", Role.USER);
        restTemplate.postForEntity("/api/members/register", request, AuthTokenResponseDTO.class);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/members/register", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("이미 존재하는 이메일입니다.");
    }

    @Test
    void login_success() {
        MemberRequestDTO registerRequest = new MemberRequestDTO("test@example.com", "password", Role.USER);
        restTemplate.postForEntity("/api/members/register", registerRequest, String.class);

        MemberLoginRequestDTO request = new MemberLoginRequestDTO("test@example.com", "password");

        ResponseEntity<AuthTokenResponseDTO> response = restTemplate.postForEntity("/api/members/login", request, AuthTokenResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().token()).isNotNull();
        assertThat(response.getBody().token()).isNotEmpty();
    }

    @Test
    void login_failed_no_email() {
        MemberLoginRequestDTO request = new MemberLoginRequestDTO("test@example.com", "password");

        ResponseEntity<String> response = restTemplate.postForEntity("/api/members/login", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).contains("존재하지 않는 이메일입니다.");
    }

    @Test
    void login_failed_unmatched_password() {
        MemberRequestDTO registerRequest = new MemberRequestDTO("test@example.com", "password", Role.USER);
        restTemplate.postForEntity("/api/members/register", registerRequest, String.class);

        MemberLoginRequestDTO request = new MemberLoginRequestDTO("test@example.com", "password1");

        ResponseEntity<String> response = restTemplate.postForEntity("/api/members/login", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).contains("비밀번호가 일치하지 않습니다.");
    }
}
