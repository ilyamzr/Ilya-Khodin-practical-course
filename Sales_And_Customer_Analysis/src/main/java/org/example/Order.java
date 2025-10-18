package org.example;

import java.time.LocalDateTime;
import java.util.List;

class Order {
    private String orderId;
    private LocalDateTime orderDate;
    private Customer customer;
    private List<OrderItem> items;
    private OrderStatus status;

    public Order(String orderId, LocalDateTime orderDate, Customer customer, List<OrderItem> items) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.customer = customer;
        this.items = items;
        this.status = OrderStatus.NEW;
    }

    public Customer getCustomer() {
        return customer;
    }
    public List<OrderItem> getItems() {
        return items;
    }
    public OrderStatus getStatus() {
        return status;
    }
    public String getOrderId() {
        return orderId;
    }
    public LocalDateTime getOrderDate() {
        return orderDate;
    }
    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}