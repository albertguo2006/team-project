package interface_adapter.stock_trading;

import javax.swing.JOptionPane;

import entity.Player;
import use_case.stock_game.play_stock_game.PlayStockGameInputBoundary;
import use_case.stock_game.play_stock_game.PlayStockGameInputData;

/**
 * Controller for the stock trading game use case.
 * Translates user actions into stock game inputs.
 */
public class StockTradingController {
    private final PlayStockGameInputBoundary stockGameInteractor;
    
    /**
     * Constructs a StockTradingController.
     * 
     * @param stockGameInteractor the stock game input boundary (interactor)
     */
    public StockTradingController(PlayStockGameInputBoundary stockGameInteractor) {
        this.stockGameInteractor = stockGameInteractor;
    }
    
    /**
     * Initiates the stock trading game for the player.
     * Prompts for investment amount and starts the game.
     * 
     * @param player the player who wants to trade stocks
     */
    public void startStockTrading(Player player) {
        // Prompt player for investment amount
        String input = JOptionPane.showInputDialog(
            null,
            "Enter investment amount (Current balance: $" + String.format("%.2f", player.getBalance()) + "):",
            "Stock Trading Game",
            JOptionPane.QUESTION_MESSAGE
        );
        
        // Handle cancel or empty input
        if (input == null || input.trim().isEmpty()) {
            return;
        }
        
        try {
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
            
            // Create input data for stock game
            // Using VOO as default stock and current game day
            PlayStockGameInputData inputData = new PlayStockGameInputData(
                "VOO",
                investmentAmount,
                player.getCurrentDay().getDayNumber()
            );
            
            // Execute the stock game
            stockGameInteractor.execute(inputData);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                null,
                "Please enter a valid number.",
                "Invalid Input",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}