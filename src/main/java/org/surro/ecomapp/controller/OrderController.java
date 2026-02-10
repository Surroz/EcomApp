package org.surro.ecomapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.surro.ecomapp.model.dto.OrderRequest;
import org.surro.ecomapp.model.dto.OrderResponse;
import org.surro.ecomapp.service.OrderService;

import java.util.List;

@RestController()
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> orders = orderService.getAllOrderResponses();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }


    @PostMapping("/orders/place")
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest orderRequest) {
        OrderResponse response = orderService.placeOrder(orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
