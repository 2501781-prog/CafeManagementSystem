package com.cafe.gui;

import com.cafe.models.Bill;
import com.cafe.models.Order;
import com.cafe.utils.UIUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class BillDialog extends JDialog {
    public BillDialog(JFrame parent, Order order) {
        super(parent, "Generated Bill", true);
        setSize(560, 590);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(12, 12));
        getContentPane().setBackground(UIUtils.BACKGROUND);

        JTextArea billArea = new JTextArea(new Bill(order).generateText());
        billArea.setEditable(false);
        billArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        billArea.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        billArea.setBackground(Color.WHITE);

        JButton closeButton = UIUtils.createButton("Close", UIUtils.PRIMARY);
        closeButton.addActionListener(e -> dispose());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIUtils.SIDEBAR);
        header.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        JLabel title = new JLabel("Cafe Bill Preview");
        title.setForeground(Color.WHITE);
        title.setFont(new Font(UIUtils.FONT_FAMILY, Font.BOLD, 18));
        JLabel hint = new JLabel("Review bill details before sharing with customer");
        hint.setForeground(new Color(200, 213, 232));
        hint.setFont(new Font(UIUtils.FONT_FAMILY, Font.PLAIN, 12));
        header.add(title, BorderLayout.NORTH);
        header.add(hint, BorderLayout.SOUTH);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(UIUtils.BACKGROUND);
        footer.setBorder(BorderFactory.createEmptyBorder(0, 14, 14, 14));
        footer.add(closeButton, BorderLayout.EAST);

        JScrollPane billScrollPane = new JScrollPane(billArea);
        billScrollPane.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER));
        billScrollPane.getViewport().setBackground(Color.WHITE);

        add(header, BorderLayout.NORTH);
        add(billScrollPane, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }
}
