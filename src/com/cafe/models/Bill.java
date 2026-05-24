package com.cafe.models;

import com.cafe.utils.DateTimeUtils;

public class Bill {
    private final Order order;

    public Bill(Order order) {
        this.order = order;
    }

    public String generateText() {
        StringBuilder builder = new StringBuilder();
        builder.append("CAFE MANAGEMENT SYSTEM\n");
        builder.append("Customer and Order Management Module\n");
        builder.append("----------------------------------------\n");
        builder.append("Bill No: ").append(order.getOrderId()).append("\n");
        builder.append("Date: ").append(DateTimeUtils.format(order.getOrderDateTime())).append("\n");
        builder.append("Customer: ").append(order.getCustomer().getName()).append("\n");
        builder.append("Phone: ").append(order.getCustomer().getPhone()).append("\n");
        builder.append("----------------------------------------\n");
        builder.append(String.format("%-18s %5s %8s %8s%n", "Item", "Qty", "Price", "Total"));
        for (OrderItem item : order.getItems()) {
            builder.append(String.format("%-18s %5d %8.2f %8.2f%n",
                    item.getMenuItem().getItemName(),
                    item.getQuantity(),
                    item.getUnitPrice(),
                    item.getLineTotal()));
        }
        builder.append("----------------------------------------\n");
        builder.append(String.format("%-32s %8.2f%n", "Grand Total:", order.getTotalAmount()));
        builder.append("----------------------------------------\n");
        builder.append("Thank you. Please visit again.\n");
        return builder.toString();
    }
}

