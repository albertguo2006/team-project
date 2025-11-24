package view;

import entity.StockInfo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Dialog for selecting a stock to trade in the stock game.
 * Displays stocks grouped by risk level with color coding.
 */
public class StockSelectionDialog extends JDialog {
    private StockInfo selectedStock;
    private boolean confirmed;

    public StockSelectionDialog(Frame parent, List<StockInfo> stocks) {
        super(parent, "Select a Stock to Trade", true);
        this.confirmed = false;

        initUI(stocks);
        pack();
        setLocationRelativeTo(parent);
    }

    private void initUI(List<StockInfo> stocks) {
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(15, 15, 15, 15));

        // Title label
        JLabel titleLabel = new JLabel("Choose a stock to trade:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(titleLabel, BorderLayout.NORTH);

        // Center panel with stock list
        JPanel stockPanel = new JPanel();
        stockPanel.setLayout(new BoxLayout(stockPanel, BoxLayout.Y_AXIS));
        stockPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Group stocks by risk level
        List<StockInfo> lowRisk = stocks.stream()
                .filter(s -> "LOW".equals(s.getRiskLevel()))
                .toList();
        List<StockInfo> mediumRisk = stocks.stream()
                .filter(s -> "MEDIUM".equals(s.getRiskLevel()))
                .toList();
        List<StockInfo> highRisk = stocks.stream()
                .filter(s -> "HIGH".equals(s.getRiskLevel()))
                .toList();

        // Add low risk stocks
        if (!lowRisk.isEmpty()) {
            addRiskSection(stockPanel, "LOW RISK", lowRisk, new Color(76, 175, 80));
        }

        // Add medium risk stocks
        if (!mediumRisk.isEmpty()) {
            addRiskSection(stockPanel, "MEDIUM RISK", mediumRisk, new Color(255, 152, 0));
        }

        // Add high risk stocks
        if (!highRisk.isEmpty()) {
            addRiskSection(stockPanel, "HIGH RISK", highRisk, new Color(244, 67, 54));
        }

        JScrollPane scrollPane = new JScrollPane(stockPanel);
        scrollPane.setPreferredSize(new Dimension(400, 400));
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addRiskSection(JPanel parent, String riskLabel, List<StockInfo> stocks, Color color) {
        // Risk level header
        JLabel headerLabel = new JLabel(riskLabel);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 12));
        headerLabel.setForeground(color);
        headerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(headerLabel);
        parent.add(Box.createVerticalStrut(5));

        // Add stock buttons
        for (StockInfo stock : stocks) {
            JButton stockButton = new JButton(formatStockLabel(stock));
            stockButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            stockButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
            stockButton.setHorizontalAlignment(SwingConstants.LEFT);
            stockButton.setBackground(Color.WHITE);
            stockButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(color, 2),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));

            // Hover effect
            stockButton.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    stockButton.setBackground(new Color(245, 245, 245));
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    stockButton.setBackground(Color.WHITE);
                }
            });

            stockButton.addActionListener(e -> {
                selectedStock = stock;
                confirmed = true;
                dispose();
            });

            parent.add(stockButton);
            parent.add(Box.createVerticalStrut(5));
        }

        parent.add(Box.createVerticalStrut(10));
    }

    private String formatStockLabel(StockInfo stock) {
        String volatilityStr = stock.getVolatility() > 0 ?
                String.format(" (Volatility: %.1f%%)", stock.getVolatility()) : "";
        return String.format("%s - %s%s", stock.getSymbol(), stock.getName(), volatilityStr);
    }

    /**
     * Shows the dialog and returns the selected stock.
     * @return the selected stock, or null if canceled
     */
    public StockInfo showDialog() {
        setVisible(true);
        return confirmed ? selectedStock : null;
    }

    /**
     * Static method to show the dialog and get user selection.
     */
    public static StockInfo selectStock(Frame parent, List<StockInfo> stocks) {
        if (stocks == null || stocks.isEmpty()) {
            JOptionPane.showMessageDialog(parent,
                    "No stocks available for trading.",
                    "No Stocks",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        StockSelectionDialog dialog = new StockSelectionDialog(parent, stocks);
        return dialog.showDialog();
    }
}
