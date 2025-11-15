package org.example.orderservice.dto;

import java.math.BigDecimal;

public class OrderItemResponse {
    private Long itemId;
    private String itemName;
    private BigDecimal itemPrice;
    private int quantity;


    public void setItemId(Long id) {
        this.itemId = id;
    }
    public Long getItemId() {
        return this.itemId;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    public String getItemName() {
        return this.itemName;
    }
    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }
    public BigDecimal getItemPrice() {
        return this.itemPrice;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public int getQuantity() {
        return this.quantity;
    }
}