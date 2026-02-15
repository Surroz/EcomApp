package org.surro.ecomapp.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.surro.ecomapp.model.Order;
import org.surro.ecomapp.model.OrderItem;
import org.surro.ecomapp.model.Product;
import org.surro.ecomapp.model.dto.OrderItemRequest;
import org.surro.ecomapp.model.dto.OrderItemResponse;
import org.surro.ecomapp.model.dto.OrderRequest;
import org.surro.ecomapp.model.dto.OrderResponse;
import org.surro.ecomapp.repo.OrderRepo;
import org.surro.ecomapp.repo.ProductRepo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderService {
    @Autowired
    private OrderRepo orderRepo;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private ProductService productService;
    @Autowired
    private PgVectorStore vectorStore;


    public OrderResponse placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderId("ORD"+ UUID.randomUUID().toString().substring(0,8));
        order.setCustomerName(orderRequest.customerName());
        order.setEmail(orderRequest.email());
        order.setOrderDate(LocalDate.now());
        order.setStatus("PLACED");

        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemRequest itemReq : orderRequest.items()) {
            Product product = productRepo.findById(itemReq.productId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            product.setStockQuantity(product.getStockQuantity() - itemReq.quantity());
            productRepo.save(product);

            productService.deleteVectorDbProduct(product.getId());
            productService.saveProductToVectorDb(product);

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(itemReq.quantity())
                    .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(itemReq.quantity())))
                    .order(order)
                    .build();

            orderItems.add(orderItem);


        }
        order.setOrderItems(orderItems);
        Order savedOrder = orderRepo.save(order);

        saveOrderToVectorDb(savedOrder);

        List<OrderItemResponse> itemResponses = new ArrayList<>();
        for(OrderItem item: order.getOrderItems()) {
            OrderItemResponse itemResponse = new OrderItemResponse(
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getProduct() .getPrice()
            );
            itemResponses.add(itemResponse);
        }

        OrderResponse orderResponse = new OrderResponse(
                savedOrder.getOrderId(),
                savedOrder.getCustomerName(),
                savedOrder.getEmail(),
                savedOrder.getStatus(),
                savedOrder.getOrderDate(),
                itemResponses
        );

        return orderResponse;
    }

    private void saveOrderToVectorDb(Order savedOrder) {
        StringBuilder content = new StringBuilder();
        content.append("Order Summary: \n");
        content.append("Order  ID: ").append(savedOrder.getOrderId()).append("\n");
        content.append("Customer: ").append(savedOrder.getCustomerName()).append("\n");
        content.append("Email: ").append(savedOrder.getEmail()).append("\n");
        content.append("Date: ").append(savedOrder.getOrderDate()).append("\n");
        content.append("Status: ").append(savedOrder.getStatus()).append("\n");
        content.append("Products: \n");

        for(OrderItem orderItem : savedOrder.getOrderItems()) {
            content.append("- product name: ").append(orderItem.getProduct().getName())
                    .append(", quantity ").append(orderItem.getQuantity())
                    .append(", price: ").append(orderItem.getTotalPrice()).append("\n");
        }

        Document document = new Document(
                UUID.randomUUID().toString(),
                content.toString(),
                Map.of("orderId", savedOrder.getOrderId())
        );

        vectorStore.add(List.of(document));
    }

    public List<OrderResponse> getAllOrderResponses() {
        List<OrderResponse> responses = new ArrayList<>();
        List<Order> orders = orderRepo.findAll();
        for (Order order : orders) {
            List<OrderItemResponse> respItems = new ArrayList<>();
            for (OrderItem item : order.getOrderItems()) {
                OrderItemResponse respItem = new OrderItemResponse(
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getTotalPrice()
                );
                respItems.add(respItem);
            }
            OrderResponse response = new OrderResponse(
                    order.getOrderId(),
                    order.getCustomerName(),
                    order.getEmail(),
                    order.getStatus(),
                    order.getOrderDate(),
                    respItems

            );
            responses.add(response);
        }
        return responses;
    }
}
