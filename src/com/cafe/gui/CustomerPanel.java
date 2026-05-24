package com.cafe.gui;

import com.cafe.models.Customer;
import com.cafe.services.CustomerService;
import com.cafe.utils.MessageUtils;
import com.cafe.utils.UIUtils;
import com.cafe.utils.ValidationUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class CustomerPanel extends JPanel {
    private final CustomerService customerService;
    private final DefaultTableModel tableModel;
    private final JTable customerTable;
    private final JTextField nameField;
    private final JTextField phoneField;
    private final JTextField emailField;
    private final JTextField addressField;
    private final JTextField searchField;
    private final JLabel statusLabel;
    private int selectedCustomerId = -1;

    public CustomerPanel() {
        customerService = new CustomerService();
        setLayout(new BorderLayout(16, 16));
        setBackground(UIUtils.BACKGROUND);
        UIUtils.pad(this);

        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Phone", "Email", "Address", "Created At"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        customerTable = new JTable(tableModel);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIUtils.styleTable(customerTable);

        nameField = createField();
        phoneField = createField();
        emailField = createField();
        addressField = createField();
        searchField = createField();
        statusLabel = UIUtils.createSmallMutedLabel("Ready");

        add(createHeader(), BorderLayout.NORTH);
        add(createFormPanel(), BorderLayout.WEST);
        add(createTablePanel(), BorderLayout.CENTER);
        loadCustomers();
        bindTableSelection();
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel titleBlock = UIUtils.createTitleBlock("Customer Management", "Create, update, search, and maintain customer records");
        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(searchField, BorderLayout.CENTER);
        JButton searchButton = UIUtils.createButton("Search", UIUtils.PRIMARY);
        JButton refreshButton = UIUtils.createButton("Refresh", UIUtils.SUCCESS);
        searchPanel.add(searchButton, BorderLayout.EAST);
        searchPanel.add(refreshButton, BorderLayout.WEST);

        searchButton.addActionListener(e -> searchCustomers());
        refreshButton.addActionListener(e -> loadCustomers());

        panel.add(titleBlock, BorderLayout.WEST);
        panel.add(searchPanel, BorderLayout.EAST);
        panel.add(statusLabel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = UIUtils.createCardPanel();
        panel.setLayout(new GridBagLayout());
        panel.setPreferredSize(new Dimension(340, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 0, 7, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1;

        JLabel formTitle = UIUtils.createSectionTitle("Customer Details");
        panel.add(formTitle, gbc);
        addLabel(panel, gbc, "Full Name");
        panel.add(nameField, gbc);
        addLabel(panel, gbc, "Phone");
        panel.add(phoneField, gbc);
        addLabel(panel, gbc, "Email");
        panel.add(emailField, gbc);
        addLabel(panel, gbc, "Address");
        panel.add(addressField, gbc);

        JButton addButton = UIUtils.createButton("Add Customer", UIUtils.SUCCESS);
        JButton updateButton = UIUtils.createButton("Update Customer", UIUtils.WARNING);
        JButton deleteButton = UIUtils.createButton("Delete Customer", UIUtils.DANGER);
        JButton clearButton = UIUtils.createButton("Clear Form", new Color(98, 112, 132));

        addButton.addActionListener(e -> addCustomer());
        updateButton.addActionListener(e -> updateCustomer());
        deleteButton.addActionListener(e -> deleteCustomer());
        clearButton.addActionListener(e -> clearForm());

        panel.add(addButton, gbc);
        panel.add(updateButton, gbc);
        panel.add(deleteButton, gbc);
        panel.add(clearButton, gbc);
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = UIUtils.createCardPanel();
        panel.setLayout(new BorderLayout(0, 12));
        panel.add(UIUtils.createSectionTitle("Customer Records"), BorderLayout.NORTH);
        panel.add(UIUtils.createTableScrollPane(customerTable), BorderLayout.CENTER);
        return panel;
    }

    private JTextField createField() {
        JTextField field = new JTextField();
        UIUtils.styleTextField(field);
        return field;
    }

    private void addLabel(JPanel panel, GridBagConstraints gbc, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(UIUtils.FONT_FAMILY, Font.BOLD, 13));
        label.setForeground(UIUtils.TEXT);
        panel.add(label, gbc);
    }

    private void bindTableSelection() {
        customerTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && customerTable.getSelectedRow() >= 0) {
                int row = customerTable.getSelectedRow();
                selectedCustomerId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                nameField.setText(tableModel.getValueAt(row, 1).toString());
                phoneField.setText(tableModel.getValueAt(row, 2).toString());
                emailField.setText(nullToEmpty(tableModel.getValueAt(row, 3)));
                addressField.setText(nullToEmpty(tableModel.getValueAt(row, 4)));
            }
        });
    }

    public void loadCustomers() {
        try {
            List<Customer> customers = customerService.getAll();
            fillTable(customers);
            setStatus("Customer list refreshed. Total records: " + customers.size());
        } catch (SQLException ex) {
            MessageUtils.showDatabaseError(this, ex);
            setStatus("Could not load customers.");
        }
    }

    private void searchCustomers() {
        try {
            String keyword = searchField.getText().trim();
            List<Customer> customers = customerService.search(keyword);
            fillTable(customers);
            setStatus(keyword.isEmpty()
                    ? "Showing all customers. Total records: " + customers.size()
                    : "Search completed for: " + keyword + " (" + customers.size() + " found)");
        } catch (SQLException ex) {
            MessageUtils.showDatabaseError(this, ex);
            setStatus("Search failed.");
        }
    }

    private void fillTable(List<Customer> customers) {
        tableModel.setRowCount(0);
        for (Customer customer : customers) {
            tableModel.addRow(new Object[]{
                customer.getCustomerId(),
                customer.getName(),
                customer.getPhone(),
                customer.getEmail(),
                customer.getAddress(),
                customer.getCreatedAt()
            });
        }
    }

    private void addCustomer() {
        if (!validateCustomerForm()) {
            return;
        }
        try {
            customerService.add(readCustomerFromForm(false));
            MessageUtils.showInfo(this, "Customer added successfully.");
            clearForm();
            // Refresh table after adding a new customer.
            loadCustomers();
        } catch (SQLException ex) {
            MessageUtils.showDatabaseError(this, ex);
            setStatus("Customer was not saved.");
        }
    }

    private void updateCustomer() {
        if (selectedCustomerId == -1) {
            MessageUtils.showValidationError(this, "Please select a customer from the table before updating.");
            return;
        }
        if (!validateCustomerForm()) {
            return;
        }
        try {
            customerService.update(readCustomerFromForm(true));
            MessageUtils.showInfo(this, "Customer updated successfully.");
            clearForm();
            // Refresh table after customer update.
            loadCustomers();
        } catch (SQLException ex) {
            MessageUtils.showDatabaseError(this, ex);
            setStatus("Customer update failed.");
        }
    }

    private void deleteCustomer() {
        if (selectedCustomerId == -1) {
            MessageUtils.showValidationError(this, "Please select a customer from the table before deleting.");
            return;
        }
        if (!MessageUtils.confirm(this, "Delete selected customer?")) {
            return;
        }
        try {
            customerService.delete(selectedCustomerId);
            MessageUtils.showInfo(this, "Customer deleted successfully.");
            clearForm();
            loadCustomers();
        } catch (SQLException ex) {
            MessageUtils.showError(this, "Customer could not be deleted because existing orders may be linked to this customer.");
            setStatus("Delete failed because this customer has order history.");
        }
    }

    private boolean validateCustomerForm() {
        if (ValidationUtils.isBlank(nameField.getText())) {
            MessageUtils.showValidationError(this, "Customer name is required.");
            setStatus("Please enter customer name.");
            return false;
        }
        if (!ValidationUtils.isValidPhone(phoneField.getText().trim())) {
            MessageUtils.showValidationError(this, "Enter a valid phone number. Example: 03001234567");
            setStatus("Phone number format is not valid.");
            return false;
        }
        if (!ValidationUtils.isValidEmail(emailField.getText().trim())) {
            MessageUtils.showValidationError(this, "Enter a valid email address or leave the email field empty.");
            setStatus("Email format is not valid.");
            return false;
        }
        return true;
    }

    private Customer readCustomerFromForm(boolean includeId) {
        Customer customer = new Customer(
                ValidationUtils.cleanText(nameField.getText()),
                ValidationUtils.cleanText(phoneField.getText()),
                ValidationUtils.optionalText(emailField.getText()),
                ValidationUtils.optionalText(addressField.getText()));
        if (includeId) {
            customer.setCustomerId(selectedCustomerId);
        }
        return customer;
    }

    private void clearForm() {
        selectedCustomerId = -1;
        customerTable.clearSelection();
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressField.setText("");
        setStatus("Form cleared.");
    }

    private String nullToEmpty(Object value) {
        return value == null ? "" : value.toString();
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
    }
}
