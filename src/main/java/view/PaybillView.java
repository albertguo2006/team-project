package view;

import data_access.PaybillDataAccessObject;
import entity.Bill;
import interface_adapter.events.paybills.PaybillController;
import interface_adapter.events.paybills.PaybillState;
import interface_adapter.events.paybills.PaybillViewModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The View for the Paybill Use Case
 */

public class PaybillView extends JPanel implements ActionListener, PropertyChangeListener {
    private final PaybillViewModel paybillViewModel;
    private final PaybillController paybillController;
    private final String viewName = "bills";
    private final PaybillDataAccessObject paybillDataAccessObject = new PaybillDataAccessObject();

    // UI Components
    private final JLabel title = new JLabel("Bills");
    private final JTable billsTable;
    private final JLabel weekLabel = new JLabel("Weekly Payments");
    private final JTable weeklyPaymentsTable;
    private final JLabel totalLabel = new JLabel("Total: $0.00")
    private final JButton payAllButton = new JButton("Pay All Bills");
    private final JLabel statusMessage = new JLabel("");

    // Table model
    private final DefaultTableModel tableModel;
    private final String[] columnNames = {"Name", "Due Date", "Amount", "Status"};

    // Bill storage
    private List<Bill> currentBills;
    private final JPanel actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

    public PaybillView(PaybillViewModel paybillViewModel, PaybillController paybillController, JTable billsTable, DefaultTableModel tableModel) {
        this.paybillViewModel = paybillViewModel;
        this.paybillController = paybillController;
        this.billsTable = billsTable;
        this.paybillViewModel.addPropertyChangeListener(this);

        // Initialize table model
        tableModel = new DefaultTableModel(columnNames, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        billsTable = new JTable(tableModel);
        setupUI();
        setupListeners();

        loadSampleBillsForWeek(1); // Load initial bills
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // HeaderPanel with title and week info
        JPanel headerPanel = new JPanel(new BorderLayout());
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerPanel.add(title, BorderLayout.WEST);

        weekLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        weekLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        headerPanel.add(weekLabel, BorderLayout.EAST);

        // Table panel with scroll
        JScrollPane tableScrollPane = new JScrollPane(billsTable);
        billsTable.setFillsViewportHeight(true);
        billsTable.setRowHeight(30);
        billsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Action buttons panel
        actionButtonsPanel.setBorder(BorderFactory.createTitledBorder("Actions"));

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
        statusMessage.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusPanel.add(statusMessage);

        // Add all panels to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(actionButtonsPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
        add(statusPanel, BorderLayout.SOUTH);

    }

    private void setupListeners(){
        payAllButton.addActionListener(this);
    }

    /**
     * Load new bills (called each week by game system)
     * @param bills list of bills for the current period - can be any number
     */
    public void loadBillsForWeek(List<bill> bills) {
        this.currentBills = bills;

        // Clear existing data
        tableModel.setRowCount(0);
        actionButtonsPanel.removeAll();

        // Add bills to table and create action buttons
        for (Bill bill: bills){
            String status = bill.getPaid() ? "Paid" : "Unpaid";
            String dueDate = formatDueDate(bill.getDueDate());
            String amount = String.format("%.2f", bill.getAmount());

            // Add to table
            tableModel.addRow(new Object[]{bill.getName(), dueDate, status, amount});

            // Create individual pay button for unpaid bills
            if (!bill.getPaid()){
                JButton payButton = createBillButton(bill);
                actionButtonsPanel.add(payButton);
            }
        }
        updateTotal();

        // Refresh panels
        actionButtonsPanel.revalidate();
        actionButtonsPanel.repaint();
    }

    private JButton createBillButton(Bill bill){
        JButton button = new JButton("Pay" + bill.getName());
        button.setActionCommand(bill.getId());
        button.addActionListener(this);

        // Style the button
        button.setBackground(new Color(70, 130, 180)); // Steel blue
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);

        return button;
    }

    private String formatDueDate(Date dueDate){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(dueDate);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == payAllButton) {
            paybillController.payAllBills();
        }
        else {
            // Individual bill payment
            String id = e.getActionCommand();
            paybillController.paySingleBill(id);
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

                    // Refresh view with updated bill statuses
                    if (currentBills != null) {
                        refreshBillsFromDataSource();
                    }
                }
                else if (state.getErrorMessage() != null) {
                    statusMessage.setForeground(Color.RED);
                    statusMessage.setText(state.getErrorMessage());
                }
            }
        }
    }

    /**
     * Refresh bills from data source after payments
     */
    private void refreshBillsFromDataSource(){
        List<Bill> updatedBills = paybillDataAccessObject.getAllBills();
        loadBills(updatedBills);

    }

    private void updateTotal(){
        if (currentBills == null) return;

        double total = 0;
        for (Bill bill: currentbills){
            total += bill.getAmount();
        }

        totalLabel.setText(String.format("%.2f", total));
    }

}
