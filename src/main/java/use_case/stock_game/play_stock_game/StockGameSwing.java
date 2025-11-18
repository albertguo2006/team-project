package use_case.stock_game.play_stock_game;

import entity.Portfolio;
import entity.Stock;


import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;

public class StockGameSwing {

    private JFrame frame;
    private JLabel priceLabel;
    private JLabel arrowLabel;
    private JLabel cashLabel;
    private JLabel sharesLabel;
    private JLabel totalEquityLabel;

    private double lastPrice;

    public StockGameSwing(List<Double> realPrices, String symbol) {

        // Create the stock and the portfolio
        Stock stock = new Stock(symbol, realPrices.get(0));

        Portfolio portfolio = new Portfolio();
        portfolio.setCash(10_000);  // starting cash
        portfolio.setInvestments(new HashMap<>()); // required initialization

        lastPrice = realPrices.get(0);

        // --- Build Swing UI ---
        frame = new JFrame("Stock Game: " + symbol);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 330);

        priceLabel = new JLabel("Price: " + lastPrice, SwingConstants.CENTER);
        priceLabel.setFont(new Font("Arial", Font.BOLD, 26));

        arrowLabel = new JLabel("—", SwingConstants.CENTER);
        arrowLabel.setFont(new Font("Arial", Font.BOLD, 48));

        JButton buyButton = new JButton("BUY (All In)");
        JButton sellButton = new JButton("SELL (All Out)");

        buyButton.setFont(new Font("Arial", Font.BOLD, 18));
        sellButton.setFont(new Font("Arial", Font.BOLD, 18));

        // Portfolio display
        cashLabel = new JLabel("Cash: " + portfolio.getCash());
        sharesLabel = new JLabel("Shares: " + portfolio.getShares(stock));
        totalEquityLabel = new JLabel("Total Equity: " + portfolio.getTotalEquity());

        JPanel portfolioPanel = new JPanel();
        portfolioPanel.setLayout(new GridLayout(3, 1));
        portfolioPanel.add(cashLabel);
        portfolioPanel.add(sharesLabel);
        portfolioPanel.add(totalEquityLabel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(buyButton);
        buttonPanel.add(sellButton);

        frame.setLayout(new BorderLayout());
        frame.add(priceLabel, BorderLayout.NORTH);
        frame.add(arrowLabel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.add(portfolioPanel, BorderLayout.EAST);

        frame.setVisible(true);

        // --- Button Logic ---
        buyButton.addActionListener(e -> {
            stock.stockPrice = lastPrice;      // update stock's internal price
            portfolio.buy(stock);              // buy using all cash
            refreshPortfolioUI(portfolio, stock);
        });

        sellButton.addActionListener(e -> {
            stock.stockPrice = lastPrice;      // update price
            portfolio.sell(stock);             // sell all shares
            refreshPortfolioUI(portfolio, stock);
        });

        // --- Price Auto-Update Every Second ---
        new Timer(1000, e -> {

            // shift to next price
            realPrices.add(realPrices.remove(0));
            double price = realPrices.get(0);
            stock.stockPrice = price;

            priceLabel.setText(String.format("Price: %.2f", price));

            if (price > lastPrice) {
                arrowLabel.setText("▲");
                arrowLabel.setForeground(Color.GREEN);
            } else if (price < lastPrice) {
                arrowLabel.setText("▼");
                arrowLabel.setForeground(Color.RED);
            } else {
                arrowLabel.setText("—");
                arrowLabel.setForeground(Color.BLACK);
            }

            lastPrice = price;

            // update equity
            refreshPortfolioUI(portfolio, stock);

        }).start();
    }

    private void refreshPortfolioUI(Portfolio portfolio, Stock stock) {

        cashLabel.setText(String.format("Cash: %.2f", portfolio.getCash()));
        sharesLabel.setText("Shares: " + portfolio.getShares(stock));
        totalEquityLabel.setText(String.format("Total Equity: %.2f", portfolio.getTotalEquity()));
    }
}