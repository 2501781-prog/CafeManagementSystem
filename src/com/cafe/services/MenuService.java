package com.cafe.services;

import com.cafe.database.DBConnection;
import com.cafe.models.MenuItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MenuService {
    public List<MenuItem> getAvailableItems() throws SQLException {
        String sql = "SELECT * FROM menu_items WHERE is_available = ? ORDER BY category, item_name";
        List<MenuItem> items = new ArrayList<MenuItem>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, true);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    items.add(new MenuItem(
                            resultSet.getInt("item_id"),
                            resultSet.getString("item_name"),
                            resultSet.getString("category"),
                            resultSet.getBigDecimal("price"),
                            resultSet.getBoolean("is_available")));
                }
            }
        }
        return items;
    }
}

