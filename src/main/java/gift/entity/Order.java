package gift.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "option_id", nullable = false)
    private Integer optionId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "order_date_time", nullable = false)
    private LocalDateTime orderDateTime;

    @Column(length = 100)
    private String message;

    protected Order() {}

    public Order(Integer optionId, Integer quantity, String message) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }
        this.optionId = optionId;
        this.quantity = quantity;
        this.orderDateTime = LocalDateTime.now();
        this.message = message;
    }

    public Integer getId() {
        return id;
    }

    public Integer getOptionId() {
        return optionId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public LocalDateTime getOrderDateTime() {
        return orderDateTime;
    }

    public String getMessage() {
        return message;
    }

}
