package view;

import interface_adapter.events.paybills.PaybillController;
import interface_adapter.events.paybills.PaybillState;
import interface_adapter.events.paybills.PaybillViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The View for the Paybill Use Case
 */

public class PaybillView extends JPanel implements ActionListener, PropertyChangeListener {
    private final PaybillViewModel paybillViewModel;
    private final PaybillController paybillController;
    private final String viewName = "bills";

    // UI Components
    private final JLabel title = new JLabel("Bills");
    private final JTable billsTable;
    private final JLabel totalLabel = new JLabel("Total: $0.00")
    private final JButton payAllButton = new JButton("Pay All Bills");
    private final JLabel statusMessage = new JLabel("");

    // Table data
    private final String[] columnNames = {"Name", "Due Date", "Amount", "Pay", "Status"};
    private final Object[][] data = new Object[][]{}; // TODO: I don't understand

    public PaybillView(PaybillViewModel paybillViewModel, PaybillController paybillController, JTable billsTable) {
        this.paybillViewModel = paybillViewModel;
        this.paybillController = paybillController;
        this.billsTable = billsTable;
        this.paybillViewModel.addPropertyChangeListener(this);

        // Create table with uneditable model
        // TODO
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titlePanel.add(title);

        // Table panel with scroll
        JScrollPane tableScrollPane = new JScrollPane(billsTable);
        billsTable.setFillsViewportHeight(true);
        billsTable.setRowHeight(30);

        // Style the table
        billsTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
        billsTable.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox()));

        // Bottom panel with total and pay all button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalPanel.add(totalLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        payAllButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        payAllButton.setBackground(new Color(76, 175, 80)); // Green
        payAllButton.setForeground(Color.WHITE);
        buttonPanel.add(payAllButton);

        bottomPanel.add(totalPanel, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        // Status message panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statusMessage.setForeground(Color.RED);
        statusPanel.add(statusMessage);

        // Add all panels to main panel
        add(titlePanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        add(statusPanel, BorderLayout.SOUTH);

        // Calculate and display initial total
        updateTotal();
    }

    private void setupListeners(){
        payAllButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == payAllButton) {
            statusMessage.setText(""); // Clear previous messages
            paybillController.payAllBills();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("state")) {
            PaybillState state = paybillViewModel.getState();
            if (state != null) {
                if (state.isPaymentSuccess()) {
                    statusMessage.setForeground(Color.GREEN);
                    statusMessage.setText(state.getSuccessMessage());
                    updateTableAfterPayment();
                    updateTotal();
                }
                else if (state.getErrorMessage() != null) {
                    statusMessage.setForeground(Color.RED);
                    statusMessage.setText(state.getErrorMessage());
                }
            }
        }
    }

    private void updateTotal(){} // TODO: Implement logic

    private void updateTableAfterPayment(){
        // Updates the table when bills are paid
        // TODO: Implement
    }
}
