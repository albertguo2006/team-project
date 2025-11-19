package entity;

/**
 * Represents a summary of a completed day's financial activity.
 * Used to display to the player what happened during the day.
 */
public class DaySummary {
    private final Day completedDay;
    private final double earnings;
    private final double spending;
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
        this.completedDay = completedDay;
        this.earnings = earnings;
        this.spending = spending;
        this.netChange = earnings - spending;
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
     * Gets the net change in balance (earnings - spending).
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
        return String.format("DaySummary[day=%s, earnings=$%.2f, spending=$%.2f, net=$%.2f, balance=$%.2f]",
                           completedDay.getDisplayName(), earnings, spending, netChange, newBalance);
    }
}