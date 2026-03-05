package org.example.domain.payment.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PaymentPrepareRequestDto {

    @NotNull
    private Long orderId;
}
