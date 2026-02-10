package org.surro.ecomapp.model.dto;

public record OrderItemRequest(
        int productId,
        int quantity
) {}
