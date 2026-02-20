package org.example.domain.category.controller.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryUpdateRequestDto {

    @Size(max = 50, message = "카테고리명은 50자 이하여야 합니다")
    private String name;

    @Size(max = 200, message = "설명은 200자 이하여야 합니다")
    private String description;
}
