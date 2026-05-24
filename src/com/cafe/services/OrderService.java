package com.cafe.services;

import com.cafe.database.DBConnection;
import com.cafe.models.Customer;
import com.cafe.models.MenuItem;
import com.cafe.models.Order;
import com.cafe.models.OrderItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class OrderService extends BaseService<Order> {
    @Override
    public boolean add(Order order) throws SQLException {
        String orderSql = "INSERT INTO orders (customer_id, order_datetime, total_amount) VALUES (?, ?, ?)";
        String itemSql = "INSERT INTO order_items (order_id, item_id, quantity, unit_price, line_total) VALUES (?, ?, ?, ?, ?)";
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            // Save order and its items together so partial bills are not stored.
            connection.setAutoCommit(false);
            try (PreparedStatement orderStatement = connection.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                orderStatement.setInt(1, order.getCustomer().getCustomerId());
                orderStatement.setTimestamp(2, Timestamp.valueOf(order.getOrderDateTime()));
                orderStatement.setBigDecimal(3, order.getTotalAmount());
                orderStatement.executeUpdate();
                try (ResultSet keys = orderStatement.getGeneratedKeys()) {
                    if (keys.next()) {
                        order.setOrderId(keys.getInt(1));
                    }
                }
            }
            try (PreparedStatement itemStatement = connection.prepareStatement(itemSql)) {
                for (OrderItem item : order.getItems()) {
                    itemStatement.setInt(1, order.getOrderId());
                    itemStatement.setInt(2, item.getMenuItem().getItemId());
                    itemStatement.setInt(3, item.getQuantity());
                    itemStatement.setBigDecimal(4, item.getUnitPrice());
                    itemStatement.setBigDecimal(5, item.getLineTotal());
                    itemStatement.addBatch();
                }
                itemStatement.executeBatch();
            }
            connection.commit();
            return true;
        } catch (SQLException ex) {
            if (connection != null) {
                connection.rollback();
            }
            throw ex;
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }

    @Override
    public boolean update(Order order) throws SQLException {
        String updateOrderSql = "UPDATE orders SET customer_id = ?, order_datetime = ?, total_amount = ? WHERE order_id = ?";
        String deleteItemsSql = "DELETE FROM order_items WHERE order_id = ?";
        String insertItemSql = "INSERT INTO order_items (order_id, item_id, quantity, unit_price, line_total) VALUES (?, ?, ?, ?, ?)";
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            // Rebuild order items inside one transaction after editing order details.
            connection.setAutoCommit(false);
            try (PreparedStatement updateStatement = connection.prepareStatement(updateOrderSql)) {
                updateStatement.setInt(1, order.getCustomer().getCustomerId());
                updateStatement.setTimestamp(2, Timestamp.valueOf(order.getOrderDateTime()));
                updateStatement.setBigDecimal(3, order.getTotalAmount());
                updateStatement.setInt(4, order.getOrderId());
                if (updateStatement.executeUpdate() == 0) {
                    connection.rollback();
                    return false;
                }
            }
            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteItemsSql)) {
                deleteStatement.setInt(1, order.getOrderId());
                deleteStatement.executeUpdate();
            }
            try (PreparedStatement insertStatement = connection.prepareStatement(insertItemSql)) {
                for (OrderItem item : order.getItems()) {
                    insertStatement.setInt(1, order.getOrderId());
                    insertStatement.setInt(2, item.getMenuItem().getItemId());
                    insertStatement.setInt(3, item.getQuantity());
                    insertStatement.setBigDecimal(4, item.getUnitPrice());
                    insertStatement.setBigDecimal(5, item.getLineTotal());
                    insertStatement.addBatch();
                }
                insertStatement.executeBatch();
            }
            connection.commit();
            return true;
        } catch (SQLException ex) {
            if (connection != null) {
                connection.rollback();
            }
            throw ex;
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM orders WHERE order_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public List<Order> search(String keyword) throws SQLException {
        String sql = "SELECT o.order_id, o.order_datetime, o.total_amount, c.customer_id, c.name, c.phone, c.email, c.address, c.created_at "
                + "FROM orders o INNER JOIN customers c ON o.customer_id = c.customer_id "
                + "WHERE c.name LIKE ? OR c.phone LIKE ? OR CAST(o.order_id AS CHAR) LIKE ? "
                + "ORDER BY o.order_datetime DESC";
        List<Order> orders = new ArrayList<Order>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            String searchText = "%" + keyword + "%";
            statement.setString(1, searchText);
            statement.setString(2, searchText);
            statement.setString(3, searchText);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    orders.add(mapOrder(resultSet));
                }
            }
        }
        return orders;
    }

    @Override
    public List<Order> getAll() throws SQLException {
        String sql = "SELECT o.order_id, o.order_datetime, o.total_amount, c.customer_id, c.name, c.phone, c.email, c.address, c.created_at "
                + "FROM orders o INNER JOIN customers c ON o.customer_id = c.customer_id "
                + "ORDER BY o.order_datetime DESC";
        List<Order> orders = new ArrayList<Order>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                orders.add(mapOrder(resultSet));
            }
        }
        return orders;
    }

    public List<Order> getRecentOrders(int limit) throws SQLException {
        String sql = "SELECT o.order_id, o.order_datetime, o.total_amount, c.customer_id, c.name, c.phone, c.email, c.address, c.created_at "
                + "FROM orders o INNER JOIN customers c ON o.customer_id = c.customer_id "
                + "ORDER BY o.order_datetime DESC LIMIT ?";
        List<Order> orders = new ArrayList<Order>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    orders.add(mapOrder(resultSet));
                }
            }
        }
        return orders;
    }

    public Order getById(int orderId) throws SQLException {
        String sql = "SELECT o.order_id, o.order_datetime, o.total_amount, c.customer_id, c.name, c.phone, c.email, c.address, c.created_at "
                + "FROM orders o INNER JOIN customers c ON o.customer_id = c.customer_id "
                + "WHERE o.order_id = ?";
        Order order = null;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, orderId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    order = mapOrder(resultSet);
                }
            }
        }
        if (order != null) {
            order.setItems(getOrderItems(orderId));
        }
        return order;
    }

    public List<OrderItem> getOrderItems(int orderId) throws SQLException {
        String sql = "SELECT oi.order_item_id, oi.order_id, oi.quantity, oi.unit_price, oi.line_total, "
                + "m.item_id, m.item_name, m.category, m.price, m.is_available "
                + "FROM order_items oi INNER JOIN menu_items m ON oi.item_id = m.item_id "
                + "WHERE oi.order_id = ?";
        List<OrderItem> items = new ArrayList<OrderItem>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, orderId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    MenuItem menuItem = new MenuItem(
                            resultSet.getInt("item_id"),
                            resultSet.getString("item_name"),
                            resultSet.getString("category"),
                            resultSet.getBigDecimal("price"),
                            resultSet.getBoolean("is_available"));
                    OrderItem item = new OrderItem();
                    item.setOrderItemId(resultSet.getInt("order_item_id"));
                    item.setOrderId(resultSet.getInt("order_id"));
                    item.setMenuItem(menuItem);
                    item.setQuantity(resultSet.getInt("quantity"));
                    item.setUnitPrice(resultSet.getBigDecimal("unit_price"));
                    item.setLineTotal(resultSet.getBigDecimal("line_total"));
                    items.add(item);
                }
            }
        }
        return items;
    }

    private Order mapOrder(ResultSet resultSet) throws SQLException {
        Customer customer = new Customer(
                resultSet.getInt("customer_id"),
                resultSet.getString("name"),
                resultSet.getString("phone"),
                resultSet.getString("email"),
                resultSet.getString("address"),
                resultSet.getTimestamp("created_at"));
        Order order = new Order(customer);
        order.setOrderId(resultSet.getInt("order_id"));
        order.setOrderDateTime(resultSet.getTimestamp("order_datetime").toLocalDateTime());
        order.setTotalAmount(resultSet.getBigDecimal("total_amount"));
        return order;
    }
}
