package gift.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "wishlist")
public class WishList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    protected WishList() {}

    public WishList(Member member, Product product, Integer quantity) {
        this.member = member;
        this.product = product;
        this.quantity = quantity;
    }

    public Integer getId() {
        return id;
    }
    public Member getMember() {
        return member;
    }
    public Product getProduct() {
        return product;
    }
    public Integer getQuantity() {
        return quantity;
    }

    public void updateQuantity(Integer quantity) {
        if (quantity == null) {
            throw new IllegalArgumentException("수량은 null일 수 없습니다.");
        }
        this.quantity = quantity;
    }
}
