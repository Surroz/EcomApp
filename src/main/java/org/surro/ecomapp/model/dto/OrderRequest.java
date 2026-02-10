package org.surro.ecomapp.model.dto;

import java.util.List;

public record OrderRequest(
        String customerName,
        String email,
        List<org.surro.ecomapp.model.dto.OrderItemRequest> items
) {}

