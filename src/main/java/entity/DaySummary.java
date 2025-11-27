package entity;

/**
 * Represents a summary of a completed day's financial activity.
 * Used to display to the player what happened during the day.
 */
public class DaySummary {
    private final Day completedDay;
    private final double earnings;
    private final double spending;
    private final double stockProfitLoss;
    private final double netChange;
    private final double newBalance;

    /**
     * Constructs a DaySummary for a completed day.
     *
     * @param completedDay the day that just ended
     * @param earnings total money earned during the day
     * @param spending total money spent during the day
     * @param newBalance the player's balance after the day
     */
    public DaySummary(Day completedDay, double earnings, double spending, double newBalance) {
        this(completedDay, earnings, spending, 0.0, newBalance);
    }

    /**
     * Constructs a DaySummary for a completed day with stock trading profit/loss.
     *
     * @param completedDay the day that just ended
     * @param earnings total money earned during the day
     * @param spending total money spent during the day
     * @param stockProfitLoss profit or loss from stock trading (positive for profit, negative for loss)
     * @param newBalance the player's balance after the day
     */
    public DaySummary(Day completedDay, double earnings, double spending, double stockProfitLoss, double newBalance) {
        this.completedDay = completedDay;
        this.earnings = earnings;
        this.spending = spending;
        this.stockProfitLoss = stockProfitLoss;
        this.netChange = earnings - spending + stockProfitLoss;
        this.newBalance = newBalance;
    }
    
    /**
     * Gets the day that was completed.
     * @return the completed Day
     */
    public Day getCompletedDay() {
        return completedDay;
    }
    
    /**
     * Gets the total earnings for the day.
     * @return earnings amount
     */
    public double getEarnings() {
        return earnings;
    }
    
    /**
     * Gets the total spending for the day.
     * @return spending amount
     */
    public double getSpending() {
        return spending;
    }

    /**
     * Gets the stock trading profit/loss for the day.
     * @return stock profit/loss (positive for profit, negative for loss)
     */
    public double getStockProfitLoss() {
        return stockProfitLoss;
    }

    /**
     * Gets the net change in balance (earnings - spending + stockProfitLoss).
     * @return net change (positive or negative)
     */
    public double getNetChange() {
        return netChange;
    }
    
    /**
     * Gets the player's new balance after the day.
     * @return new balance
     */
    public double getNewBalance() {
        return newBalance;
    }
    
    @Override
    public String toString() {
        return String.format("DaySummary[day=%s, earnings=$%.2f, spending=$%.2f, stockProfitLoss=$%.2f, net=$%.2f, balance=$%.2f]",
                           completedDay.getDisplayName(), earnings, spending, stockProfitLoss, netChange, newBalance);
    }
}