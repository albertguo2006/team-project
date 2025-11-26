package interface_adapter.stock_trading;

import javax.swing.JOptionPane;

import api.StockDataManager;
import entity.Player;
import entity.StockInfo;
import use_case.stock_game.play_stock_game.PlayStockGameInputBoundary;
import use_case.stock_game.play_stock_game.PlayStockGameInputData;
import view.StockSelectionDialog;

import java.util.List;
import java.util.Map;

/**
 * Controller for the stock trading game use case.
 * Translates user actions into stock game inputs.
 */
public class StockTradingController {
    private final PlayStockGameInputBoundary stockGameInteractor;
    private final StockDataManager stockDataManager;

    /**
     * Constructs a StockTradingController.
     *
     * @param stockGameInteractor the stock game input boundary (interactor)
     * @param apiKey the Alpha Vantage API key
     */
    public StockTradingController(PlayStockGameInputBoundary stockGameInteractor, String apiKey) {
        this.stockGameInteractor = stockGameInteractor;
        this.stockDataManager = new StockDataManager(apiKey);
    }
    
    /**
     * Initiates the stock trading game for the player.
     * Shows stock selection dialog, prompts for investment amount, and starts the game.
     *
     * @param player the player who wants to trade stocks
     */
    public void startStockTrading(Player player) {
        try {
            // Step 1: Show stock selection dialog
            List<StockInfo> availableStocks = stockDataManager.getAllStocks();
            StockInfo selectedStock = StockSelectionDialog.selectStock(null, availableStocks);

            if (selectedStock == null) {
                // User canceled stock selection
                return;
            }

            // Step 2: Ensure stock has data available (fetch if needed)
            boolean hasData = stockDataManager.ensureStockData(selectedStock.getSymbol());

            if (!hasData) {
                JOptionPane.showMessageDialog(
                        null,
                        "Failed to fetch stock data. Please try again later.",
                        "Data Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Step 3: Select a random period (will use unplayed if available, otherwise replay)
            Map<String, Object> periodInfo = stockDataManager.selectRandomUnplayedPeriod(selectedStock.getSymbol());

            if (periodInfo == null) {
                // This should rarely happen now with fallback enabled
                JOptionPane.showMessageDialog(
                        null,
                        "Unable to load data for " + selectedStock.getSymbol() + ".\n" +
                                "The stock data may be corrupted or missing.\n" +
                                "Please check the console for error details.",
                        "Data Error",
                        JOptionPane.ERROR_MESSAGE
                );
                // Refund the player since we couldn't start the game
                player.setBalance(player.getBalance());
                return;
            }

            // Step 4: Prompt player for investment amount
            String input = JOptionPane.showInputDialog(
                    null,
                    "Selected: " + selectedStock.getSymbol() + " (" + selectedStock.getRiskLevel() + " RISK)\n\n" +
                            "Enter investment amount (Current balance: $" + String.format("%.2f", player.getBalance()) + "):",
                    "Investment Amount",
                    JOptionPane.QUESTION_MESSAGE
            );

            // Handle cancel or empty input
            if (input == null || input.trim().isEmpty()) {
                return;
            }

            double investmentAmount = Double.parseDouble(input.trim());

            // Validate investment amount
            if (investmentAmount <= 0) {
                JOptionPane.showMessageDialog(
                        null,
                        "Investment amount must be greater than zero.",
                        "Invalid Amount",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            if (investmentAmount > player.getBalance()) {
                JOptionPane.showMessageDialog(
                        null,
                        "Insufficient funds. Your balance is $" + String.format("%.2f", player.getBalance()),
                        "Insufficient Funds",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Deduct investment from player balance
            player.setBalance(player.getBalance() - investmentAmount);

            // Step 5: Create input data for stock game with period info
            String month = (String) periodInfo.get("month");
            int startDayIndex = (Integer) periodInfo.get("startDay");
            String periodId = (String) periodInfo.get("periodId");

            // Check for StockSlowdown buff - doubles the timer interval (slows game down)
            int timerInterval = 250;  // Normal speed
            if (player.hasBuff("StockSlowdown")) {
                timerInterval = 500;  // Slowed down (2x slower)
                player.removeBuff("StockSlowdown");  // One-time use
                System.out.println("Focus Pill effect activated! Stock game slowed down.");
            }

            PlayStockGameInputData inputData = new PlayStockGameInputData(
                    selectedStock.getSymbol(),
                    investmentAmount,
                    month,
                    startDayIndex,
                    periodId,
                    timerInterval
            );

            // Step 6: Execute the stock game
            stockGameInteractor.execute(inputData);

            // Step 7: Mark period as played after game ends
            stockDataManager.markPeriodAsPlayed(selectedStock.getSymbol(), periodId);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Please enter a valid number.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "An error occurred: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }
}