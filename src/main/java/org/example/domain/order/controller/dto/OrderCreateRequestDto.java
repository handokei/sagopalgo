package org.example.domain.order.controller.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreateRequestDto {

    @NotEmpty(message = "주문 상품은 최소 1개 이상이어야 합니다")
    @Valid
    private List<OrderItemRequestDto> items;

    @NotBlank(message = "수령인 이름은 필수입니다")
    private String name;

    @NotBlank(message = "전화번호는 필수입니다")
    private String phoneNumber;

    @NotBlank(message = "배송지 주소는 필수입니다")
    private String address;
}
