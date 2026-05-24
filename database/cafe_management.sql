CREATE DATABASE IF NOT EXISTS cafe_management;
USE cafe_management;

DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS menu_items;
DROP TABLE IF EXISTS customers;

CREATE TABLE customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(120),
    address VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE menu_items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    item_name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(60) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    is_available BOOLEAN DEFAULT TRUE
);

CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    order_datetime DATETIME NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE TABLE order_items (
    order_item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    item_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    line_total DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES menu_items(item_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

INSERT INTO customers (name, phone, email, address) VALUES
('Ali Khan', '03001234567', 'ali.khan@example.com', 'Gulberg, Lahore'),
('Ayesha Malik', '03111234567', 'ayesha.malik@example.com', 'Model Town, Lahore'),
('Hassan Raza', '03221234567', 'hassan.raza@example.com', 'DHA, Karachi'),
('Fatima Noor', '03331234567', 'fatima.noor@example.com', 'Satellite Town, Rawalpindi');

INSERT INTO menu_items (item_name, category, price, is_available) VALUES
('Cappuccino', 'Coffee', 450.00, TRUE),
('Espresso', 'Coffee', 300.00, TRUE),
('Latte', 'Coffee', 500.00, TRUE),
('Club Sandwich', 'Fast Food', 650.00, TRUE),
('Chicken Burger', 'Fast Food', 750.00, TRUE),
('Chocolate Cake', 'Dessert', 550.00, TRUE),
('Brownie', 'Dessert', 350.00, TRUE),
('Mint Margarita', 'Cold Drink', 400.00, TRUE);

INSERT INTO orders (customer_id, order_datetime, total_amount) VALUES
(1, NOW(), 950.00),
(2, NOW(), 1100.00);

INSERT INTO order_items (order_id, item_id, quantity, unit_price, line_total) VALUES
(1, 1, 1, 450.00, 450.00),
(1, 3, 1, 500.00, 500.00),
(2, 4, 1, 650.00, 650.00),
(2, 1, 1, 450.00, 450.00);

