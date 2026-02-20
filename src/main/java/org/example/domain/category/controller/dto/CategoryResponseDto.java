package org.example.domain.category.controller.dto;

import lombok.Getter;
import org.example.domain.category.domain.model.Category;

import java.time.LocalDateTime;

@Getter
public class CategoryResponseDto {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;

    public CategoryResponseDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.description = category.getDescription();
        this.createdAt = category.getCreatedAt();
    }

    public static CategoryResponseDto from(Category category) {
        return new CategoryResponseDto(category);
    }
}
