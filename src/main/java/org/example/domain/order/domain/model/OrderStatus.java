package org.example.domain.order.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {

    CREATED("주문생성"),
    PAID("결제완료"),
    SHIPPED("배송중"),
    COMPLETED("배송완료"),
    CANCELED("취소")
    ;

    private String orderStatus;
}
