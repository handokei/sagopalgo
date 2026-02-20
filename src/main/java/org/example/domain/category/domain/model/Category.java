package org.example.domain.category.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.domain.category.exception.CategoryErrorCode;
import org.example.domain.category.exception.CategoryException;
import org.example.global.config.entity.BaseEntity;

@Entity
@Table(name = "categories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    private boolean isDeleted = false;

    private Category(String name, String description) {
        if (name == null || name.isBlank()) {
            throw new CategoryException(CategoryErrorCode.CATEGORY_NAME_REQUIRED);
        }
        this.name = name;
        this.description = description;
    }

    public static Category of(String name, String description) {
        return new Category(name, description);
    }

    public void update(String name, String description) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (description != null) {
            this.description = description;
        }
    }

    public void delete() {
        this.isDeleted = true;
    }
}
