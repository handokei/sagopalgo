package org.example.domain.payment.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PaymentConfirmRequestDto {

    @NotNull
    private Long paymentId;

    @NotBlank
    private String portOnePaymentId;
}
