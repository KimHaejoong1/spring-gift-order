package gift.repository;

import gift.entity.Member;
import gift.entity.Product;
import gift.entity.WishList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<WishList, Integer> {
    @EntityGraph(attributePaths = {"product"})
    List<WishList> findByMember(Member member);
    @EntityGraph(attributePaths = {"product"})
    Page<WishList> findByMember(Member member, Pageable pageable);
    Optional<WishList> findByMemberAndProduct(Member member, Product product);
}
