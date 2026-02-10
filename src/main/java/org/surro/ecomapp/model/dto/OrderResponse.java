package org.surro.ecomapp.model.dto;


import java.time.LocalDate;
import java.util.List;

public record OrderResponse(
        String orderId,
        String customerName,
        String email,
        String status,
        LocalDate orderDate,
        List<org.surro.ecomapp.model.dto.OrderItemResponse> items
) {}
