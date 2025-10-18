package org.example;

class OrderItem {
    private String productName;
    private int quantity;
    private double price;
    private Category category;

    public OrderItem(String productName, int quantity, double price, Category category) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.category = category;
    }

    public double getPrice()
    {
        return price;
    }
    public int getQuantity()
    {
        return quantity;
    }
    public String getProductName()
    {
        return productName;
    }
    public Category getCategory()
    {
        return category;
    }
}
