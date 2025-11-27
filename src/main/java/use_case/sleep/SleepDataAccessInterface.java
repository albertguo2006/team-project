package use_case.sleep;

import entity.Day;
import entity.DaySummary;
import entity.Player;

/**
 * Data access interface for the sleep use case.
 * Defines operations needed to persist player state and retrieve day summaries.
 */
public interface SleepDataAccessInterface {
    
    /**
     * Saves the player's current state.
     * 
     * @param player the player to save
     */
    void savePlayerState(Player player);
    
    /**
     * Creates a day summary from the given information.
     *
     * @param day the day that was completed
     * @param earnings the total earnings for the day
     * @param spending the total spending for the day
     * @param newBalance the player's new balance
     * @return a DaySummary object
     */
    DaySummary createDaySummary(Day day, double earnings, double spending, double newBalance);

    /**
     * Creates a day summary from the given information including stock trading profit/loss.
     *
     * @param day the day that was completed
     * @param earnings the total earnings for the day
     * @param spending the total spending for the day
     * @param stockProfitLoss profit or loss from stock trading
     * @param newBalance the player's new balance
     * @return a DaySummary object
     */
    DaySummary createDaySummary(Day day, double earnings, double spending, double stockProfitLoss, double newBalance);
}
