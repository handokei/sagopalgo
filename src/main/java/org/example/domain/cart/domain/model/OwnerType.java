package org.example.domain.cart.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OwnerType {
    USER("유저"),
    GUEST("게스트");

    private String ownerType;

}
