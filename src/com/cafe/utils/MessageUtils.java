package com.cafe.utils;

import java.awt.Component;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public final class MessageUtils {
    private MessageUtils() {
    }

    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showValidationError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Validation Required", JOptionPane.WARNING_MESSAGE);
    }

    public static void showDatabaseError(Component parent, SQLException ex) {
        JOptionPane.showMessageDialog(parent, friendlyDatabaseMessage(ex), "Database Error", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private static String friendlyDatabaseMessage(SQLException ex) {
        String message = ex.getMessage() == null ? "" : ex.getMessage();
        String lower = message.toLowerCase();
        if (lower.contains("duplicate") || "23000".equals(ex.getSQLState())) {
            return "This record already exists or is linked with another record. Please check the entered data.";
        }
        if (lower.contains("communications link failure") || lower.contains("connection")) {
            return "Database connection failed. Please make sure MySQL is running and the database settings are correct.";
        }
        if (lower.contains("unknown database")) {
            return "Database not found. Please run the SQL script before starting the application.";
        }
        if (lower.contains("access denied")) {
            return "Database login failed. Please check username and password in DBConnection.java.";
        }
        return "Operation failed because of a database error. Details: " + message;
    }
}
