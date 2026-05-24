# Customer and Order Management Module Report

## Introduction

This module is part of a Cafe Management System desktop application. It manages customers, order placement, order history, and bill generation using Java Swing, JDBC, and MySQL.

## Problem Statement

Manual cafe customer and order handling causes slow billing, repeated customer records, calculation mistakes, and weak order history tracking. This module solves those issues with a structured desktop system.

## Objectives

- Maintain customer records with CRUD operations.
- Place orders with item, quantity, price, and automatic total calculation.
- Store order history with date and time.
- Generate readable bills.
- Use OOP concepts and secure JDBC queries.

## ER Diagram

```text
customers 1-----* orders 1-----* order_items *-----1 menu_items
```

## Module Distribution

| Module | Lead Member | Supporting Members | GUI Work | Backend Work | Database Work |
|---|---|---|---|---|---|
| Customer and Order Management | Student Name | Team Members | Customer forms, order dashboard, JTable views | Validation, services, bill generation | Customers, menu, orders, order_items tables |

## Integration Strategy

- This module uses the shared `cafe_management` database.
- Other modules can reuse `DBConnection`.
- Navigation can be connected from a larger dashboard by opening `CustomerPanel` and `OrderPanel`.
- All database queries use `PreparedStatement`.

## Implementation Details

- `CustomerService` handles customer CRUD/search.
- `MenuService` loads available cafe menu items.
- `OrderService` saves orders and order items in a transaction.
- `Bill` generates formatted bill text.
- `ValidationUtils` centralizes input validation.

## Testing

| Test Case | Expected Result |
|---|---|
| Add valid customer | Customer appears in JTable |
| Add customer with empty name | Validation error shown |
| Search by phone/name | Matching customers appear |
| Place order with empty cart | Validation error shown |
| Place valid order | Order saved and bill generated |
| Generate bill from history | Saved order bill appears |

## Results

The module compiles successfully and provides a complete Swing-based workflow for customer management, order placement, automatic totals, order history, and bill generation.

## Conclusion

The project demonstrates OOP design, modular development, GUI/backend/database integration, validation, exception handling, and secure MySQL connectivity suitable for university viva and final submission.

