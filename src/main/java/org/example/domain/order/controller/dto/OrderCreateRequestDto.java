package org.example.domain.order.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreateRequestDto {

    private Long productId;

    private int quantity;

    private String name;

    private String phoneNumber;

    private String address;
}
