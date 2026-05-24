package com.cafe.gui;

import com.cafe.models.Customer;
import com.cafe.models.MenuItem;
import com.cafe.models.Order;
import com.cafe.models.OrderItem;
import com.cafe.services.CustomerService;
import com.cafe.services.MenuService;
import com.cafe.services.OrderService;
import com.cafe.utils.DateTimeUtils;
import com.cafe.utils.MessageUtils;
import com.cafe.utils.UIUtils;
import com.cafe.utils.ValidationUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class OrderPanel extends JPanel {
    private final CustomerService customerService;
    private final MenuService menuService;
    private final OrderService orderService;
    private final JComboBox<Customer> customerCombo;
    private final JComboBox<MenuItem> itemCombo;
    private final JTextField priceField;
    private final JSpinner quantitySpinner;
    private final JLabel totalLabel;
    private final JLabel statusLabel;
    private final JTextField historySearchField;
    private final DefaultTableModel cartModel;
    private final DefaultTableModel historyModel;
    private final JTable cartTable;
    private final JTable historyTable;
    private final List<OrderItem> cartItems;

    public OrderPanel() {
        customerService = new CustomerService();
        menuService = new MenuService();
        orderService = new OrderService();
        cartItems = new ArrayList<OrderItem>();

        setLayout(new BorderLayout(16, 16));
        setBackground(UIUtils.BACKGROUND);
        UIUtils.pad(this);

        customerCombo = new JComboBox<Customer>();
        itemCombo = new JComboBox<MenuItem>();
        priceField = new JTextField();
        priceField.setEditable(false);
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        totalLabel = new JLabel("Rs. 0.00");
        statusLabel = UIUtils.createSmallMutedLabel("Ready");
        historySearchField = new JTextField();

        UIUtils.styleTextField(priceField);
        UIUtils.styleTextField(historySearchField);
        UIUtils.styleComboBox(customerCombo);
        UIUtils.styleComboBox(itemCombo);
        UIUtils.styleSpinner(quantitySpinner);

        cartModel = new DefaultTableModel(new Object[]{"Item ID", "Item", "Qty", "Price", "Line Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        historyModel = new DefaultTableModel(new Object[]{"Order ID", "Customer", "Phone", "Date Time", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cartTable = new JTable(cartModel);
        historyTable = new JTable(historyModel);
        UIUtils.styleTable(cartTable);
        UIUtils.styleTable(historyTable);

        add(createHeader(), BorderLayout.NORTH);
        add(createOrderArea(), BorderLayout.CENTER);
        refreshAllData();
        bindItemPrice();
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel titleBlock = UIUtils.createTitleBlock("Order Management & Billing",
                "Place orders, calculate bill, and review order history");

        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setOpaque(false);
        JButton searchButton = UIUtils.createButton("Search Orders", UIUtils.PRIMARY);
        JButton refreshButton = UIUtils.createButton("Refresh", UIUtils.SUCCESS);
        searchPanel.add(refreshButton, BorderLayout.WEST);
        searchPanel.add(historySearchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        searchButton.addActionListener(e -> searchOrders());
        refreshButton.addActionListener(e -> refreshAllData());

        panel.add(titleBlock, BorderLayout.WEST);
        panel.add(searchPanel, BorderLayout.EAST);
        panel.add(statusLabel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createOrderArea() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 16, 16));
        panel.setOpaque(false);
        panel.add(createCartPanel());
        panel.add(createHistoryPanel());
        return panel;
    }

    private JPanel createCartPanel() {
        JPanel panel = UIUtils.createCardPanel();
        panel.setLayout(new BorderLayout(12, 12));

        panel.add(createOrderForm(), BorderLayout.NORTH);
        panel.add(UIUtils.createTableScrollPane(cartTable), BorderLayout.CENTER);
        panel.add(createCartFooter(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createOrderForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        addFormLabel(form, gbc, 0, 0, "Customer");
        gbc.gridx = 1;
        gbc.gridy = 0;
        form.add(customerCombo, gbc);

        addFormLabel(form, gbc, 0, 1, "Menu Item");
        gbc.gridx = 1;
        gbc.gridy = 1;
        form.add(itemCombo, gbc);

        addFormLabel(form, gbc, 0, 2, "Price");
        gbc.gridx = 1;
        gbc.gridy = 2;
        form.add(priceField, gbc);

        addFormLabel(form, gbc, 0, 3, "Quantity");
        gbc.gridx = 1;
        gbc.gridy = 3;
        form.add(quantitySpinner, gbc);

        JButton addItemButton = UIUtils.createButton("Add Item", UIUtils.SUCCESS);
        JButton removeItemButton = UIUtils.createButton("Remove Selected", UIUtils.DANGER);
        addItemButton.addActionListener(e -> addItemToCart());
        removeItemButton.addActionListener(e -> removeSelectedCartItem());

        gbc.gridx = 0;
        gbc.gridy = 4;
        form.add(addItemButton, gbc);
        gbc.gridx = 1;
        form.add(removeItemButton, gbc);
        return form;
    }

    private JPanel createCartFooter() {
        JPanel footer = new JPanel(new BorderLayout(12, 12));
        footer.setOpaque(false);

        totalLabel.setFont(new Font(UIUtils.FONT_FAMILY, Font.BOLD, 24));
        totalLabel.setForeground(UIUtils.PRIMARY);

        JPanel actions = new JPanel(new GridLayout(1, 2, 10, 0));
        actions.setOpaque(false);
        JButton placeOrderButton = UIUtils.createButton("Place Order & Bill", UIUtils.PRIMARY);
        JButton clearButton = UIUtils.createSecondaryButton("Clear Cart");
        placeOrderButton.addActionListener(e -> placeOrder());
        clearButton.addActionListener(e -> clearCart());
        actions.add(placeOrderButton);
        actions.add(clearButton);

        footer.add(new JLabel("Grand Total:"), BorderLayout.WEST);
        footer.add(totalLabel, BorderLayout.CENTER);
        footer.add(actions, BorderLayout.SOUTH);
        return footer;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = UIUtils.createCardPanel();
        panel.setLayout(new BorderLayout(12, 12));

        JLabel title = UIUtils.createSectionTitle("Order History");
        JPanel actions = new JPanel(new GridLayout(1, 2, 8, 0));
        actions.setOpaque(false);
        JButton billButton = UIUtils.createButton("Generate Bill", UIUtils.WARNING);
        JButton deleteButton = UIUtils.createButton("Delete Order", UIUtils.DANGER);
        billButton.addActionListener(e -> generateBillFromHistory());
        deleteButton.addActionListener(e -> deleteSelectedOrder());
        actions.add(billButton);
        actions.add(deleteButton);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(title, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);

        panel.add(header, BorderLayout.NORTH);
        panel.add(UIUtils.createTableScrollPane(historyTable), BorderLayout.CENTER);
        return panel;
    }

    private void addFormLabel(JPanel panel, GridBagConstraints gbc, int x, int y, String text) {
        gbc.gridx = x;
        gbc.gridy = y;
        JLabel label = new JLabel(text);
        label.setFont(new Font(UIUtils.FONT_FAMILY, Font.BOLD, 13));
        label.setForeground(UIUtils.TEXT);
        panel.add(label, gbc);
    }

    public void refreshAllData() {
        loadCustomers();
        loadMenuItems();
        loadOrderHistory();
    }

    private void loadCustomers() {
        try {
            customerCombo.removeAllItems();
            for (Customer customer : customerService.getAll()) {
                customerCombo.addItem(customer);
            }
        } catch (SQLException ex) {
            MessageUtils.showDatabaseError(this, ex);
            setStatus("Customer list could not be loaded.");
        }
    }

    private void loadMenuItems() {
        try {
            itemCombo.removeAllItems();
            for (MenuItem item : menuService.getAvailableItems()) {
                itemCombo.addItem(item);
            }
            updateSelectedPrice();
        } catch (SQLException ex) {
            MessageUtils.showDatabaseError(this, ex);
            setStatus("Menu items could not be loaded.");
        }
    }

    private void loadOrderHistory() {
        try {
            List<Order> orders = orderService.getAll();
            fillHistory(orders);
            setStatus("Order history loaded. Total orders: " + orders.size());
        } catch (SQLException ex) {
            MessageUtils.showDatabaseError(this, ex);
            setStatus("Order history could not be loaded.");
        }
    }

    private void searchOrders() {
        try {
            String keyword = historySearchField.getText().trim();
            List<Order> orders = orderService.search(keyword);
            fillHistory(orders);
            setStatus("Order search completed. Records found: " + orders.size());
        } catch (SQLException ex) {
            MessageUtils.showDatabaseError(this, ex);
            setStatus("Order search failed.");
        }
    }

    private void fillHistory(List<Order> orders) {
        historyModel.setRowCount(0);
        for (Order order : orders) {
            historyModel.addRow(new Object[]{
                order.getOrderId(),
                order.getCustomer().getName(),
                order.getCustomer().getPhone(),
                DateTimeUtils.format(order.getOrderDateTime()),
                order.getTotalAmount()
            });
        }
    }

    private void bindItemPrice() {
        itemCombo.addActionListener(e -> updateSelectedPrice());
    }

    private void updateSelectedPrice() {
        MenuItem selectedItem = (MenuItem) itemCombo.getSelectedItem();
        // Keep price field synced when user changes menu item.
        priceField.setText(selectedItem == null ? "" : selectedItem.getPrice().toString());
    }

    private void addItemToCart() {
        MenuItem selectedItem = (MenuItem) itemCombo.getSelectedItem();
        if (selectedItem == null) {
            MessageUtils.showValidationError(this, "Please select a menu item.");
            setStatus("No menu item selected.");
            return;
        }
        String quantityText = quantitySpinner.getValue().toString();
        if (!ValidationUtils.isPositiveInteger(quantityText)) {
            MessageUtils.showValidationError(this, "Quantity must be greater than zero.");
            setStatus("Invalid quantity.");
            return;
        }
        int quantity = Integer.parseInt(quantityText);
        OrderItem orderItem = new OrderItem(selectedItem, quantity);
        cartItems.add(orderItem);
        cartModel.addRow(new Object[]{
            selectedItem.getItemId(),
            selectedItem.getItemName(),
            quantity,
            orderItem.getUnitPrice(),
            orderItem.getLineTotal()
        });
        // Calculate total bill dynamically after cart changes.
        updateTotalLabel();
        setStatus(selectedItem.getItemName() + " added to cart.");
    }

    private void removeSelectedCartItem() {
        int row = cartTable.getSelectedRow();
        if (row < 0) {
            MessageUtils.showValidationError(this, "Please select an item from cart.");
            setStatus("No cart item selected.");
            return;
        }
        cartItems.remove(row);
        cartModel.removeRow(row);
        updateTotalLabel();
        setStatus("Selected cart item removed.");
    }

    private void updateTotalLabel() {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : cartItems) {
            total = total.add(item.getLineTotal());
        }
        totalLabel.setText("Rs. " + total);
    }

    private void placeOrder() {
        Customer selectedCustomer = (Customer) customerCombo.getSelectedItem();
        if (selectedCustomer == null) {
            MessageUtils.showValidationError(this, "Please select a customer before placing order.");
            setStatus("Customer is required for order.");
            return;
        }
        if (cartItems.isEmpty()) {
            MessageUtils.showValidationError(this, "Please add at least one item to cart.");
            setStatus("Cart is empty.");
            return;
        }
        Order order = new Order(selectedCustomer);
        order.setOrderDateTime(LocalDateTime.now());
        order.setItems(new ArrayList<OrderItem>(cartItems));
        try {
            orderService.add(order);
            MessageUtils.showInfo(this, "Order placed successfully. Bill generated.");
            showBill(order);
            clearCart();
            loadOrderHistory();
        } catch (SQLException ex) {
            MessageUtils.showDatabaseError(this, ex);
            setStatus("Order was not saved.");
        }
    }

    private void generateBillFromHistory() {
        int row = historyTable.getSelectedRow();
        if (row < 0) {
            MessageUtils.showValidationError(this, "Please select an order from history.");
            setStatus("No order selected for bill.");
            return;
        }
        int orderId = Integer.parseInt(historyModel.getValueAt(row, 0).toString());
        try {
            Order order = orderService.getById(orderId);
            if (order == null) {
                MessageUtils.showError(this, "Selected order was not found.");
                setStatus("Selected order was not found.");
                return;
            }
            showBill(order);
        } catch (SQLException ex) {
            MessageUtils.showDatabaseError(this, ex);
            setStatus("Bill could not be generated.");
        }
    }

    private void deleteSelectedOrder() {
        int row = historyTable.getSelectedRow();
        if (row < 0) {
            MessageUtils.showValidationError(this, "Please select an order from history.");
            setStatus("No order selected for delete.");
            return;
        }
        int orderId = Integer.parseInt(historyModel.getValueAt(row, 0).toString());
        if (!MessageUtils.confirm(this, "Delete selected order and its bill details?")) {
            return;
        }
        try {
            if (orderService.delete(orderId)) {
                MessageUtils.showInfo(this, "Order deleted successfully.");
                loadOrderHistory();
            } else {
                MessageUtils.showError(this, "Selected order was not found.");
                setStatus("Selected order was not found.");
            }
        } catch (SQLException ex) {
            MessageUtils.showDatabaseError(this, ex);
            setStatus("Order delete failed.");
        }
    }

    private void showBill(Order order) {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        new BillDialog(frame, order).setVisible(true);
    }

    private void clearCart() {
        cartItems.clear();
        cartModel.setRowCount(0);
        quantitySpinner.setValue(1);
        updateTotalLabel();
        setStatus("Cart cleared.");
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
    }
}
