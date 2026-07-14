package org.example.domain.look.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LookItemRequestDto {

    @NotNull(message = "상품 ID는 필수입니다")
    private Long productId;

    @NotBlank(message = "아이템 라벨은 필수입니다")
    private String label;

    private String price;

    private String imageUrl;
}
