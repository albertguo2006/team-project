package use_case.stock_game.play_stock_game;

/**
 import api.AlphaStockDataBase;
import entity.Portfolio;
import entity.Stock;

import javax.swing.Timer;
import javax.swing.*;
import java.awt.*;
import java.lang.Math;
import java.util.List;
import java.util.Random;

public class StockGameSwing {

    private JFrame frame;
    private JLabel priceLabel;
    private JLabel arrowLabel;
    private JLabel cashLabel;
    private JLabel sharesLabel;
    private JLabel totalEquityLabel;

    private double lastPrice;

    public StockGameSwing(List<Double> realPrices, String symbol, Double startAmount) {
        lastPrice = realPrices.get(0);

        // create instances portfolio investments, and load the stock into there
        Portfolio portfolio = new Portfolio();
        Stock stock = new Stock(symbol, lastPrice);
        portfolio.loadStock(stock);
        portfolio.setCash(startAmount);  // starting investing amount (inputData from user)


        // using java swing to make ui
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

        // show details regarding portfolio, (with numbers rounded for easier visuals)
        cashLabel = new JLabel("Cash: " + Math.round(portfolio.getCash()*100)/100.0);
        sharesLabel = new JLabel("Shares: " + Math.round(portfolio.getShares(stock)*100)/100.0);
        totalEquityLabel = new JLabel("Total Equity: " + Math.round(portfolio.getTotalEquity()*100)/100.0);

        JPanel portfolioPanel = new JPanel();
        portfolioPanel.setLayout(new GridLayout(3, 1));
        portfolioPanel.add(cashLabel);
        portfolioPanel.add(sharesLabel);
        portfolioPanel.add(totalEquityLabel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(buyButton);

        frame.setLayout(new BorderLayout());
        frame.add(priceLabel, BorderLayout.NORTH);
        frame.add(arrowLabel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.add(portfolioPanel, BorderLayout.EAST);

        frame.setVisible(true);

        // buttons to buy and sell
        buyButton.addActionListener(e -> {
            stock.stockPrice = lastPrice;      // update stock's price
            portfolio.buy(stock);              // buy using all cash
            refreshPortfolioUI(portfolio, stock);

            buttonPanel.remove(buyButton);
            buttonPanel.add(sellButton);            // add SELL button, and remove buy button
            frame.repaint();


        });

        sellButton.addActionListener(e -> {
            stock.stockPrice = lastPrice;      // update price
            portfolio.sell(stock);             // sell all shares
            refreshPortfolioUI(portfolio, stock);

            buttonPanel.remove(sellButton);
            buttonPanel.add(buyButton);            // add BUY button, and remove sell button
            frame.repaint();
        });

        // update each second (__ ms)
        final int[] ticks = {0};
        Timer timer = new Timer(250, e -> {
            ticks[0] += 1;

            if (ticks[0] >= realPrices.size() || portfolio.getTotalEquity()<=0) { // stop when done the list of prices (around 193)
                ((Timer)e.getSource()).stop();
                JOptionPane.showMessageDialog(frame, "Game Over! You finished your work for today.");
                if (portfolio.getTotalEquity() > 0){
                    // TODO: need to be able to get input stock data (start amount)
                    JOptionPane.showMessageDialog(frame, "Congrats! You earned: " +
                            portfolio.getTotalEquity() +  "dollars!");
                }
                else{
                    JOptionPane.showMessageDialog(frame, "Unfortunately, You lost: " +
                            portfolio.getTotalEquity() +  "dollars... better luck next time!");
                }

                JOptionPane.showMessageDialog(frame, "Game Over! You finished your work for today.");
                return;
            }


            // shift to next price
            realPrices.add(realPrices.remove(0));   // removed already used price from the list of prices
            double nextRealPrice = realPrices.get(0);
            double diff =  nextRealPrice - lastPrice;
            // make random object
            Random random = new Random();
            // use random algorithm to calculate new price based on previous and next price
            double momentum = (nextRealPrice - lastPrice) * 0.3;
            double noise = random.nextGaussian() * 0.01 * nextRealPrice;
            double price = nextRealPrice + diff * 0.04 + momentum + noise;


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

            // update details shown on screen, shares, cash, equity etc
            refreshPortfolioUI(portfolio, stock);

        });
        timer.start();
    }

    private void refreshPortfolioUI(Portfolio portfolio, Stock stock) {

        // show details regarding portfolio, (with numbers rounded for easier visuals)
        cashLabel.setText("Cash: " + Math.round(portfolio.getCash()*100)/100.0);
        sharesLabel.setText("Shares: " + Math.round(portfolio.getShares(stock)*100)/100.0);
        totalEquityLabel.setText("Total Equity: " + Math.round(portfolio.getTotalEquity()*100)/100.0);
    }


    public static void main(String[] args) throws Exception {

        List<Double> opens = AlphaStockDataBase.getIntradayOpensForGameDay("VOO", 5);

        System.out.println("opens: " + opens.size());
        SwingUtilities.invokeLater(() -> new StockGameSwing(opens, "VOO", 10000.0));
    }
}
 **/