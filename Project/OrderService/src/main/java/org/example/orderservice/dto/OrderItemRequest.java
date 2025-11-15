package org.example.orderservice.dto;

import org.antlr.v4.runtime.misc.NotNull;

import java.util.Objects;
import jakarta.validation.constraints.*;

public class OrderItemRequest {
    @NotNull
    private Long itemId;
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    public OrderItemRequest() {
    }

    public OrderItemRequest(Long itemId, int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemRequest that = (OrderItemRequest) o;
        return quantity == that.quantity && Objects.equals(itemId, that.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, quantity);
    }

    @Override
    public String toString() {
        return "OrderItemRequest{" +
                "itemId=" + itemId +
                ", quantity=" + quantity +
                '}';
    }
}