package com.cafe.services;

import com.cafe.database.DBConnection;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardService {
    public int getTotalCustomers() throws SQLException {
        return readInt("SELECT COUNT(*) AS total FROM customers");
    }

    public int getTotalOrders() throws SQLException {
        return readInt("SELECT COUNT(*) AS total FROM orders");
    }

    public BigDecimal getTotalRevenue() throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) AS total FROM orders";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return resultSet.next() ? resultSet.getBigDecimal("total") : BigDecimal.ZERO;
        }
    }

    public int getAvailableMenuItems() throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM menu_items WHERE is_available = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, true);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt("total") : 0;
            }
        }
    }

    private int readInt(String sql) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return resultSet.next() ? resultSet.getInt("total") : 0;
        }
    }
}
