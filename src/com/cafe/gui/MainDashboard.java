package com.cafe.gui;

import com.cafe.utils.UIUtils;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class MainDashboard extends JFrame {
    private final JPanel contentPanel;
    private final CardLayout cardLayout;
    private final DashboardPanel dashboardPanel;
    private final CustomerPanel customerPanel;
    private final OrderPanel orderPanel;
    private JButton dashboardButton;
    private JButton customersButton;
    private JButton ordersButton;

    public MainDashboard() {
        setTitle("Cafe Management System - Customer and Order Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1180, 720));
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        dashboardPanel = new DashboardPanel();
        customerPanel = new CustomerPanel();
        orderPanel = new OrderPanel();

        contentPanel.add(dashboardPanel, "DASHBOARD");
        contentPanel.add(customerPanel, "CUSTOMERS");
        contentPanel.add(orderPanel, "ORDERS");

        add(createSidebar(), BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
        cardLayout.show(contentPanel, "DASHBOARD");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(UIUtils.SIDEBAR);
        sidebar.setPreferredSize(new Dimension(260, 720));
        sidebar.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(28, 22, 20, 22));
        topPanel.setLayout(new BorderLayout());

        JLabel title = new JLabel("<html>Cafe<br>Management</html>");
        title.setForeground(Color.WHITE);
        title.setFont(new Font(UIUtils.FONT_FAMILY, Font.BOLD, 28));

        JLabel module = new JLabel("Customer & Order Module");
        module.setForeground(new Color(190, 203, 224));
        module.setFont(new Font(UIUtils.FONT_FAMILY, Font.PLAIN, 13));

        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(module, BorderLayout.SOUTH);

        JPanel navPanel = new JPanel();
        navPanel.setOpaque(false);
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        navPanel.setLayout(new java.awt.GridLayout(0, 1, 0, 12));

        dashboardButton = navButton("DB  Dashboard");
        customersButton = navButton("CU  Customers");
        ordersButton = navButton("OR  Orders & Billing");

        dashboardButton.addActionListener(e -> {
            dashboardPanel.refreshStats();
            setActiveNavigation(dashboardButton);
            cardLayout.show(contentPanel, "DASHBOARD");
        });
        customersButton.addActionListener(e -> {
            customerPanel.loadCustomers();
            setActiveNavigation(customersButton);
            cardLayout.show(contentPanel, "CUSTOMERS");
        });
        ordersButton.addActionListener(e -> {
            orderPanel.refreshAllData();
            setActiveNavigation(ordersButton);
            cardLayout.show(contentPanel, "ORDERS");
        });

        navPanel.add(dashboardButton);
        navPanel.add(customersButton);
        navPanel.add(ordersButton);

        JLabel footer = new JLabel("<html>OOP Lab Project<br>Java Swing + MySQL JDBC</html>");
        footer.setForeground(new Color(150, 166, 190));
        footer.setFont(new Font(UIUtils.FONT_FAMILY, Font.PLAIN, 12));
        footer.setBorder(BorderFactory.createEmptyBorder(16, 22, 24, 22));

        sidebar.add(topPanel, BorderLayout.NORTH);
        sidebar.add(navPanel, BorderLayout.CENTER);
        sidebar.add(footer, BorderLayout.SOUTH);
        setActiveNavigation(dashboardButton);
        return sidebar;
    }

    private JButton navButton(String text) {
        return UIUtils.createNavButton(text);
    }

    private void setActiveNavigation(JButton activeButton) {
        JButton[] buttons = {dashboardButton, customersButton, ordersButton};
        for (JButton button : buttons) {
            if (button == null) {
                continue;
            }
            boolean active = button == activeButton;
            button.putClientProperty("active", active);
            button.putClientProperty("activeColor", UIUtils.PRIMARY);
            button.setBackground(active ? UIUtils.PRIMARY : new Color(30, 41, 59));
            button.setForeground(Color.WHITE);
            button.setFont(new Font(UIUtils.FONT_FAMILY, active ? Font.BOLD : Font.PLAIN, 13));
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, active ? 4 : 0, 0, 0, active ? new Color(147, 197, 253) : new Color(30, 41, 59)),
                    BorderFactory.createEmptyBorder(12, active ? 12 : 16, 12, 16)));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("Look and feel could not be loaded: " + ex.getMessage());
            }
            new MainDashboard().setVisible(true);
        });
    }
}
