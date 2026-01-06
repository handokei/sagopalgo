package org.example.domain.cart.support;

import org.example.domain.cart.domain.model.OwnerType;
import org.example.domain.cart.exception.CartErrorCode;
import org.example.domain.cart.exception.CartException;
import org.example.global.security.jwt.CustomUserDetails;
import org.springframework.stereotype.Component;
@Component
public class CartOwnerResolver {

    public CartOwner resolve(
            CustomUserDetails userDetails,
            String guestKey
    ) {

        if (userDetails != null) {
            return new CartOwner(
                    OwnerType.USER,
                    userDetails.getId().toString()
            );
        }

        if (guestKey == null || guestKey.isBlank()) {
            throw new CartException(CartErrorCode.NOT_FOUND_GUEST_KEY_EXCEPTION);
        }

        return new CartOwner(OwnerType.GUEST, guestKey);
    }
}
