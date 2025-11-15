package org.example.orderservice.dto;

import org.example.orderservice.entity.OrderStatus;
import java.util.Objects;

public class UpdateOrderStatusRequest {
    private OrderStatus status;

    public UpdateOrderStatusRequest() {
    }

    public UpdateOrderStatusRequest(OrderStatus status) {
        this.status = status;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateOrderStatusRequest that = (UpdateOrderStatusRequest) o;
        return status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }

    @Override
    public String toString() {
        return "UpdateOrderStatusRequest{" +
                "status=" + status +
                '}';
    }
}