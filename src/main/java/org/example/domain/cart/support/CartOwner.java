package org.example.domain.cart.support;


import org.example.domain.cart.domain.model.OwnerType;

public record CartOwner(   OwnerType ownerType,
                           String ownerKey)
{
}
