package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import entity.Portfolio;
import entity.Stock;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.Styler;
import use_case.stock_game.play_stock_game.PlayStockGameOutputBoundary;
import use_case.stock_game.play_stock_game.PlayStockGameOutputData;

/**
 * view for the stock game
 */
public class StockGameView implements PlayStockGameOutputBoundary {

    private final StockGameViewModel viewModel = new StockGameViewModel();

    private JFrame frame;
    private JLabel priceLabel;
    private JLabel cashLabel;
    private JLabel sharesLabel;
    private JLabel equityLabel;

    private CategoryChart chart;
    private XChartPanel<CategoryChart> chartPanel;

    private Portfolio portfolio;
    private Stock stock;
    private entity.Player player;  // Reference to player for crediting balance
    private double initialInvestment;  // Track initial investment for profit/loss calculation

    /**
     * Sets the player reference for crediting balance on game end.
     * @param player the player
     */
    public void setPlayer(entity.Player player) {
        this.player = player;
    }
    
    // view at the start of the game
    @Override
    public void presentGameStart(Portfolio portfolio, Stock stock, Timer timer) {
        this.portfolio = portfolio;
        this.stock = stock;
        this.initialInvestment = portfolio.getCash();  // Store initial investment

        viewModel.lastPrice = stock.stockPrice;  // set initial price

        buildUI();
        timer.start();
    }

    // update the view after price is changed
    @Override
    public void presentPriceUpdate(PlayStockGameOutputData data) {

        // change viewmodel
        viewModel.update(data);
        viewModel.addPriceToHistory(data.price);

        // set text
        priceLabel.setText("Price: " + String.format("%.2f", viewModel.price));
        cashLabel.setText("Cash: " + String.format("%.2f", viewModel.cash));
        sharesLabel.setText("Shares: " + String.format("%.2f", viewModel.shares));
        equityLabel.setText("Total Equity: " + String.format("%.2f", viewModel.equity));

        // update chart with price history
        updateChartData();

        viewModel.lastPrice = viewModel.price; // store new price as last price
    }

    private void updateChartData() {
        // Convert queue to list for chart
        List<Double> prices = new ArrayList<>(viewModel.priceHistory);

        // Create x-axis data (time indices)
        List<Integer> xData = new ArrayList<>();
        for (int i = 0; i < prices.size(); i++) {
            xData.add(i);
        }

        // Update chart series
        chart.updateCategorySeries("Price", xData, prices, null);
        chartPanel.repaint();
    }

    // end of game view
    @Override
    public void presentGameOver(PlayStockGameOutputData data) {
        double finalEquity = data.totalEquity;
        
        // Credit player balance with final equity
        if (player != null) {
            player.setBalance(player.getBalance() + finalEquity);
            
            // Calculate profit/loss
            double profitLoss = finalEquity - initialInvestment;
            String profitLossText = profitLoss >= 0 ?
                "Profit: $" + String.format("%.2f", profitLoss) :
                "Loss: $" + String.format("%.2f", Math.abs(profitLoss));
            
            JOptionPane.showMessageDialog(frame,
                "Game Over!\n" +
                "Final Equity: $" + String.format("%.2f", finalEquity) + "\n" +
                profitLossText + "\n" +
                "New Balance: $" + String.format("%.2f", player.getBalance()));
        } else {
            JOptionPane.showMessageDialog(frame, "Game Over!\nFinal Equity: $" + String.format("%.2f", finalEquity));
        }
        
        // Close the stock game window
        if (frame != null) {
            frame.dispose();
        }
    }

    // if there is an error...
    @Override
    public void presentError(String message) {
        JOptionPane.showMessageDialog(frame, "Error: " + message);
    }

    @Override
    public void prepareSuccessView(PlayStockGameOutputData outputData) {
        // Present success view for valid investment
        presentPriceUpdate(outputData);
    }

    @Override
    public void prepareFailView(String errorMessage) {
        // Present failure view for invalid investment
        presentError(errorMessage);
    }

    // details for making the panels, buttons etc.
    private void buildUI() {
        JButton sellButton;
        JButton buyButton;

        frame = new JFrame("Stock Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 330);
        frame.setLayout(new BorderLayout());

        priceLabel = new JLabel("Price: 0", SwingConstants.CENTER);
        priceLabel.setFont(new Font("Arial", Font.BOLD, 26));

        // Create chart for price history
        chart = new CategoryChartBuilder()
                .width(400)
                .height(200)
                .title("Stock Price History")
                .xAxisTitle("Time")
                .yAxisTitle("Price ($)")
                .build();

        // Style the chart
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        chart.getStyler().setDefaultSeriesRenderStyle(org.knowm.xchart.style.lines.SeriesLines.NONE);
        chart.getStyler().setMarkerSize(0);

        // Initialize with empty data
        List<Integer> xData = new ArrayList<>();
        List<Double> yData = new ArrayList<>();
        chart.addSeries("Price", xData, yData);

        chartPanel = new XChartPanel<>(chart);

        cashLabel = new JLabel("Cash: 0");
        sharesLabel = new JLabel("Shares: 0");
        equityLabel = new JLabel("Total Equity: 0");

        JPanel east = new JPanel(new GridLayout(3, 1));
        east.add(cashLabel);
        east.add(sharesLabel);
        east.add(equityLabel);

        // buy and sell buttons
        buyButton = new JButton("BUY (All In)");
        sellButton = new JButton("SELL (All Out)");

        // at the start, only buy button is visible (must start by buying)
        sellButton.setVisible(false);

        buyButton.addActionListener(e -> {
            stock.stockPrice = viewModel.price;
            portfolio.buy(stock);

            // switch which buttons are visible (so only 1 button at a time)
            buyButton.setVisible(false);
            sellButton.setVisible(true);
        });

        sellButton.addActionListener(e -> {
            stock.stockPrice = viewModel.price;
            portfolio.sell(stock);

            // switch which buttons are visible (so only 1 button at a time)
            sellButton.setVisible(false);
            buyButton.setVisible(true);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(buyButton);     // add buttons
        buttonPanel.add(sellButton);

        frame.add(priceLabel, BorderLayout.NORTH);
        frame.add(chartPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.add(east, BorderLayout.EAST);

        frame.setVisible(true);
    }
}
