package gift.entity;

import jakarta.persistence.*;

import java.math.BigInteger;
import java.util.List;

@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 15)
    private String name;

    @Column(nullable = false)
    private BigInteger price;

    @Column(nullable = false, name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "product",fetch = FetchType.LAZY)
    private List<Option> options;

    protected Product() {}

    public Product(String name, BigInteger price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public Product(Integer id, String name, BigInteger price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigInteger getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<Option> getOptions() {
        return options;
    }

    public boolean hasOptionWithName(String name) {
        return options != null &&
                options.stream().anyMatch(option -> option
                                .getName()
                                .equals(name)
                );
    }
}
