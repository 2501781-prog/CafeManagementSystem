package com.cafe.models;

import java.math.BigDecimal;

public class MenuItem {
    private int itemId;
    private String itemName;
    private String category;
    private BigDecimal price;
    private boolean available;

    public MenuItem() {
    }

    public MenuItem(int itemId, String itemName, String category, BigDecimal price, boolean available) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.category = category;
        this.price = price;
        this.available = available;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return itemName + " - Rs. " + price;
    }
}

