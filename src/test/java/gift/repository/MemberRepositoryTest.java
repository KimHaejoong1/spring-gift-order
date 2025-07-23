package gift.repository;
import gift.entity.Member;
import gift.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
public class MemberRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void save_success() {
        Member member = new Member("test@example.com", "encodedPassword", Role.USER);

        Member saved = memberRepository.save(member);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("test@example.com");
        assertThat(saved.getPassword()).isEqualTo("encodedPassword");
        assertThat(saved.getRole()).isEqualTo(Role.USER);
    }

    @Test
    void findByEmail_success() {
        Member member = new Member("find@example.com", "password123", Role.USER);
        entityManager.persistAndFlush(member);

        Optional<Member> found = memberRepository.findByEmail("find@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("find@example.com");
        assertThat(found.get().getRole()).isEqualTo(Role.USER);
    }

    @Test
    void findByEmail_not_found() {
        Optional<Member> found = memberRepository.findByEmail("notfound@example.com");

        assertThat(found).isEmpty();
    }

    @Test
    void findByEmail_unique_constraint() {
        Member member1 = new Member("unique@example.com", "password1", Role.USER);
        entityManager.persistAndFlush(member1);

        Member member2 = new Member("unique@example.com", "password2", Role.ADMIN);

        assertThatThrownBy(() -> {
            entityManager.persistAndFlush(member2);
        }).isInstanceOf(Exception.class);
    }

    @Test
    void findById_success() {
        Member member = new Member("findbyid@example.com", "password", Role.ADMIN);
        Member saved = entityManager.persistAndFlush(member);

        Optional<Member> found = memberRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("findbyid@example.com");
        assertThat(found.get().getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void existsById_success() {
        Member member = new Member("exists@example.com", "password", Role.USER);
        Member saved = entityManager.persistAndFlush(member);

        assertThat(memberRepository.existsById(saved.getId())).isTrue();
        assertThat(memberRepository.existsById(999)).isFalse();
    }
}
