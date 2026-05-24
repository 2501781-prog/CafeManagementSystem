# Cafe Management System - Customer and Order Management Module

Professional Java OOP desktop module built with **Java Swing** and **MySQL JDBC**.

## Setup

1. Install JDK 8+, NetBeans, MySQL Server, and MySQL Connector/J.
2. Run `database/cafe_management.sql` in MySQL.
3. Open this folder in NetBeans.
4. Add MySQL Connector/J JAR in Project Properties > Libraries.
5. Update credentials in `src/com/cafe/database/DBConnection.java` if required.
6. Run `com.cafe.gui.MainDashboard`.

## Packages

- `com.cafe.gui` - Swing dashboard cards, customer panel, order panel, bill dialog
- `com.cafe.models` - Encapsulated domain classes
- `com.cafe.database` - JDBC connection
- `com.cafe.services` - DAO/service layer with PreparedStatement queries
- `com.cafe.utils` - Validation, UI, date/time, messages

## OOP Concepts

- Encapsulation: private fields with getters/setters in model classes.
- Abstraction: `BaseService<T>` defines a common service contract.
- Inheritance: concrete service classes extend `BaseService<T>`.
- Polymorphism: customer/order services override common CRUD/search methods.

## Viva Highlights

- PreparedStatement is used in every SQL operation to prevent SQL injection.
- Validation is separated in utility classes.
- GUI and database logic are separated.
- The module includes dummy data and can be integrated with other modules using the same shared database.

## Features

- Add, update, delete, search, and display customers in JTable.
- Place orders with item selection, quantity, price, and automatic total calculation.
- View order history, delete orders, and generate bills.
- Dashboard cards show total customers, orders, revenue, and available menu items.
- All SQL operations use PreparedStatement.

## Recommended File Creation Order

1. `database/cafe_management.sql`
2. `src/com/cafe/database/DBConnection.java`
3. Model classes in `src/com/cafe/models`
4. Utility classes in `src/com/cafe/utils`
5. Service classes in `src/com/cafe/services`
6. GUI classes in `src/com/cafe/gui`
7. NetBeans project files in `nbproject`

## Common Error Fixes

- `MySQL JDBC Driver not found`: add MySQL Connector/J JAR in NetBeans project libraries.
- `Access denied for user root`: update username/password in `DBConnection.java`.
- `Unknown database cafe_management`: run `database/cafe_management.sql`.
- `Communications link failure`: start MySQL server and confirm port `3306`.
- `Cannot delete customer`: that customer has existing orders; delete linked orders first or keep the customer for history.
