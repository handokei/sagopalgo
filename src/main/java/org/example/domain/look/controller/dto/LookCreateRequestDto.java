package org.example.domain.look.controller.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LookCreateRequestDto {

    @NotBlank(message = "룩 제목은 필수입니다")
    private String title;

    private String subtitle;

    private String curator;

    @NotBlank(message = "룩 대표 이미지는 필수입니다")
    private String heroImageUrl;

    @Valid
    @NotEmpty(message = "룩 아이템은 최소 1개 이상이어야 합니다")
    private List<LookItemRequestDto> items;
}
