package org.example.orderservice.dto;

public class OrderWithUserResponse {
    private OrderResponse order;
    private UserDto user;

    public void setOrder(OrderResponse orderResponse) {
        this.order = orderResponse;
    }
    public void setUser(UserDto user) {
        this.user = user;
    }
    public OrderResponse getOrder() {
        return order;
    }
    public UserDto getUser() {
        return user;
    }
}