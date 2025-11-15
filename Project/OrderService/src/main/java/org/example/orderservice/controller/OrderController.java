package org.example.orderservice.controller;

import jakarta.validation.Valid;
import org.example.orderservice.dto.CreateOrderRequest;
import org.example.orderservice.dto.OrderWithUserResponse;
import org.example.orderservice.dto.UpdateOrderStatusRequest;
import org.example.orderservice.entity.OrderStatus;
import org.example.orderservice.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderWithUserResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderWithUserResponse response = orderService.createOrder(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderWithUserResponse> getOrderById(@PathVariable Long id) {
        OrderWithUserResponse response = orderService.getOrderById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping(params = "ids")
    public ResponseEntity<List<OrderWithUserResponse>> getOrdersByIds(@RequestParam List<Long> ids) {
        List<OrderWithUserResponse> orders = orderService.getOrdersByIds(ids);
        return ResponseEntity.ok(orders);
    }

    @GetMapping(params = "statuses")
    public ResponseEntity<List<OrderWithUserResponse>> getOrdersByStatuses(@RequestParam List<OrderStatus> statuses) {
        List<OrderWithUserResponse> orders = orderService.getOrdersByStatuses(statuses);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderWithUserResponse> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        OrderWithUserResponse response = orderService.updateOrderStatus(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}