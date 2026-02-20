package org.example.domain.viewhistory.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.global.config.entity.BaseEntity;

@Entity
@Table(name = "view_histories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ViewHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long productId;

    private Long categoryId;

    private ViewHistory(Long userId, Long productId, Long categoryId) {
        this.userId = userId;
        this.productId = productId;
        this.categoryId = categoryId;
    }

    public static ViewHistory of(Long userId, Long productId, Long categoryId) {
        return new ViewHistory(userId, productId, categoryId);
    }
}
