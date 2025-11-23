package view;

import data_access.Paybill.PaybillDataAccessObject;
import entity.Bill;
import interface_adapter.ViewManagerModel;
import interface_adapter.paybills.PaybillController;
import interface_adapter.paybills.PaybillState;
import interface_adapter.paybills.PaybillViewModel;
import io.opencensus.stats.ViewManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * The View for the Paybill Use Case
 */

public class PaybillView extends JPanel implements ActionListener, PropertyChangeListener {
    private final PaybillViewModel paybillViewModel;
    private final PaybillController paybillController;
    private final ViewManagerModel viewManagerModel;
    private final String viewName = "bills";
    private final PaybillDataAccessObject paybillDataAccessObject = new PaybillDataAccessObject();

    // UI Components
    private final JLabel title = new JLabel("Bills");
    private final JTable billsTable;
    private final JLabel weekLabel = new JLabel("Weekly Payments");
    private final JLabel totalLabel = new JLabel("Total: $0.00");
    private final JButton payAllButton = new JButton("Pay All Bills");
    private final JLabel statusMessage = new JLabel("");

    // Table model
    private final DefaultTableModel tableModel;

    // Bill storage
    private List<Bill> currentBills;
    private final JPanel actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

    public PaybillView(PaybillViewModel paybillViewModel, PaybillController paybillController,
                       ViewManagerModel viewManagerModel){
        this.paybillViewModel = paybillViewModel;
        this.paybillController = paybillController;
        this.viewManagerModel = viewManagerModel;
        this.paybillViewModel.addPropertyChangeListener(this);

        // Initialize table model
        String[] columnNames = {"Name", "Due Date", "Amount", "Status"};
        this.tableModel = new DefaultTableModel(columnNames, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        billsTable = new JTable(this.tableModel);
        setupUI();
        setupListeners();

        loadInitialBills(); // Load initial bills

        // Add keyboard support for returning to game
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"),
                "goBack");
        this.getActionMap().put("goBack", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // Switch back to game view
                viewManagerModel.setState("game");
                viewManagerModel.firePropertyChange();
            }
        });
    }

    void loadInitialBills(){
        System.out.println("PaybillView.loadInitialBills() called");
        List<Bill> initialBills = paybillDataAccessObject.getAllBills();
        loadBills(initialBills);
        System.out.println("Retrieved " + initialBills.size() + " bills from data access");
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
        actionButtonsPanel.setLayout(new BoxLayout(actionButtonsPanel, BoxLayout.Y_AXIS));
        actionButtonsPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
        actionButtonsPanel.setPreferredSize(new Dimension(180, 0));

        // Combined bottom panel with total, pay all, and status
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // Left: Total
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        totalPanel.add(totalLabel);

        // Right: Pay all button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        payAllButton.setFont(new Font("Segoe UI", Font.BOLD, 20));
        payAllButton.setBackground(new Color(76, 175, 80)); // Green
        payAllButton.setForeground(Color.WHITE);
        payAllButton.setPreferredSize(new Dimension(172, 40));
        buttonPanel.add(payAllButton);

        // Status message panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statusMessage.setForeground(Color.RED);
        statusMessage.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        statusPanel.add(statusMessage);

        bottomPanel.add(totalPanel, BorderLayout.WEST);
        bottomPanel.add(statusPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        // Create main content panel with table and actions side by side
        JPanel contentPanel = new JPanel(new BorderLayout(10,10));
        contentPanel.add(tableScrollPane, BorderLayout.CENTER);
        contentPanel.add(actionButtonsPanel, BorderLayout.EAST);

        // Add all panels to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

    }

    private void setupListeners(){
        payAllButton.addActionListener(this);
    }

    private JButton createBillButton(Bill bill){
        JButton button = new JButton("Pay " + bill.getName());
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
        statusMessage.setText(""); // Clear previous message
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

    public void loadBills(List<Bill> bills){
        System.out.println("PaybillView.loadBills() called with " + bills.size() + " bills");
        this.currentBills = bills;

        // Clear existing data
        this.tableModel.setRowCount(0);
        System.out.println("Table model cleared, row count: " + this.tableModel.getRowCount());
        actionButtonsPanel.removeAll();

        // Add bills to table and create action buttons
        for (Bill bill: bills){
            String status = bill.getPaid() ? "Paid" : "Unpaid";
            String dueDate = formatDueDate(bill.getDueDate());
            String amount = String.format("$%.2f", bill.getAmount());

            System.out.println("Adding bill to table: " + bill.getName() + " - " + amount + " - " + status);

            // Add to table
            this.tableModel.addRow(new Object[]{bill.getName(), dueDate, amount, status});

            // Create individual button for unpaid bills
            if (!bill.getPaid()){
                JButton payButton = createBillButton(bill);
                actionButtonsPanel.add(payButton);
                System.out.println("Created pay button for: " + bill.getName());
            }

        }
        // Update UI Labels
        updateTotal();

        // Refresh the panels
        actionButtonsPanel.revalidate();
        actionButtonsPanel.repaint();
        System.out.println("Table model row count after loading: " + tableModel.getRowCount());
    }

    private void updateTotal(){
        if (currentBills == null) return;

        // Enable/Disable pay all button based on whether there are unpaid bills
        double total = 0;
        boolean hasUnpaidBills = false;
        for (Bill bill: currentBills) {
            if (!bill.getPaid()){
                total += bill.getAmount();
                hasUnpaidBills = true;
            }
        }

        totalLabel.setText(String.format("Total: $%.2f", total));
        payAllButton.setEnabled(hasUnpaidBills && total > 0);
    }

}
