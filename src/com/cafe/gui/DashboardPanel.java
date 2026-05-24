package com.cafe.gui;

import com.cafe.services.DashboardService;
import com.cafe.services.OrderService;
import com.cafe.models.Order;
import com.cafe.utils.DateTimeUtils;
import com.cafe.utils.MessageUtils;
import com.cafe.utils.UIUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class DashboardPanel extends JPanel {
    private final DashboardService dashboardService;
    private final OrderService orderService;
    private final JLabel customerCountLabel;
    private final JLabel orderCountLabel;
    private final JLabel revenueLabel;
    private final JLabel menuCountLabel;
    private final DefaultTableModel recentOrdersModel;

    public DashboardPanel() {
        dashboardService = new DashboardService();
        orderService = new OrderService();
        customerCountLabel = createMetricLabel();
        orderCountLabel = createMetricLabel();
        revenueLabel = createMetricLabel();
        menuCountLabel = createMetricLabel();
        recentOrdersModel = new DefaultTableModel(new Object[]{"Order ID", "Customer", "Date Time", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        setLayout(new BorderLayout(16, 16));
        setBackground(UIUtils.BACKGROUND);
        UIUtils.pad(this);

        add(createHeader(), BorderLayout.NORTH);
        add(createBody(), BorderLayout.CENTER);
        refreshStats();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        header.add(UIUtils.createTitleBlock("Customer & Order Dashboard",
                "Quick summary for viva and daily cafe operations"), BorderLayout.WEST);
        return header;
    }

    private JPanel createBody() {
        JPanel body = new JPanel(new BorderLayout(16, 16));
        body.setOpaque(false);

        JPanel cards = new JPanel(new GridLayout(1, 4, 14, 14));
        cards.setOpaque(false);
        cards.add(createCard("Customers", customerCountLabel, UIUtils.PRIMARY));
        cards.add(createCard("Orders", orderCountLabel, UIUtils.SUCCESS));
        cards.add(createCard("Revenue", revenueLabel, UIUtils.WARNING));
        cards.add(createCard("Menu Items", menuCountLabel, new Color(105, 89, 205)));

        JTable table = new JTable(recentOrdersModel);
        UIUtils.styleTable(table);
        JPanel recentPanel = new JPanel(new BorderLayout(10, 10));
        recentPanel.setBackground(Color.WHITE);
        recentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 230, 240)),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JLabel recentTitle = new JLabel("Recent Orders");
        recentTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        recentTitle.setForeground(UIUtils.TEXT);
        recentPanel.add(recentTitle, BorderLayout.NORTH);
        recentPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        body.add(cards, BorderLayout.NORTH);
        body.add(recentPanel, BorderLayout.CENTER);
        return body;
    }

    private JPanel createCard(String title, JLabel valueLabel, Color accent) {
        JPanel card = UIUtils.createCardPanel();
        card.setLayout(new BorderLayout(8, 8));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(new Color(92, 105, 125));
        valueLabel.setForeground(accent);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JLabel createMetricLabel() {
        JLabel label = new JLabel("0");
        label.setFont(new Font("Segoe UI", Font.BOLD, 28));
        return label;
    }

    public void refreshStats() {
        try {
            customerCountLabel.setText(String.valueOf(dashboardService.getTotalCustomers()));
            orderCountLabel.setText(String.valueOf(dashboardService.getTotalOrders()));
            BigDecimal revenue = dashboardService.getTotalRevenue();
            revenueLabel.setText("Rs. " + revenue);
            menuCountLabel.setText(String.valueOf(dashboardService.getAvailableMenuItems()));
            loadRecentOrders();
        } catch (SQLException ex) {
            MessageUtils.showDatabaseError(this, ex);
        }
    }

    private void loadRecentOrders() throws SQLException {
        recentOrdersModel.setRowCount(0);
        // Only recent records are needed on dashboard, so the query stays light.
        List<Order> orders = orderService.getRecentOrders(10);
        for (Order order : orders) {
            recentOrdersModel.addRow(new Object[]{
                order.getOrderId(),
                order.getCustomer().getName(),
                DateTimeUtils.format(order.getOrderDateTime()),
                order.getTotalAmount()
            });
        }
    }
}
