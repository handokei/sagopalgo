package org.example.domain.payment.client;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.domain.payment.exception.PaymentErrorCode;
import org.example.domain.payment.exception.PaymentException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class PortOneClient {

    private static final String BASE_URL = "https://api.portone.io";

    @Value("${portone.api-secret}")
    private String apiSecret;

    public PortOnePaymentResponse getPayment(String portOnePaymentId) {
        try {
            return RestClient.create(BASE_URL)
                    .get()
                    .uri("/payments/{paymentId}", portOnePaymentId)
                    .header("Authorization", "PortOne " + apiSecret)
                    .retrieve()
                    .body(PortOnePaymentResponse.class);
        } catch (Exception e) {
            throw new PaymentException(PaymentErrorCode.PORTONE_VERIFY_FAILED);
        }
    }

    @Getter
    public static class PortOnePaymentResponse {
        private String id;
        private String status;
        private Amount amount;

        @Getter
        public static class Amount {
            private int total;
        }
    }
}
