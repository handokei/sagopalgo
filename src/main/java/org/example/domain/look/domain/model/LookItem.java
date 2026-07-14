package org.example.domain.look.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "look_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class LookItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "look_id", nullable = false)
    private Look look;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private String label;

    private String price;

    private String imageUrl;

    private int sortOrder;

    private LookItem(Long productId, String label, String price, String imageUrl, int sortOrder) {
        this.productId = productId;
        this.label = label;
        this.price = price;
        this.imageUrl = imageUrl;
        this.sortOrder = sortOrder;
    }

    public static LookItem of(Long productId, String label, String price, String imageUrl, int sortOrder) {
        return new LookItem(productId, label, price, imageUrl, sortOrder);
    }

    void assignLook(Look look) {
        this.look = look;
    }
}
