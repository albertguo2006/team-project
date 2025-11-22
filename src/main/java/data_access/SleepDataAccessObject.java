package data_access;

import entity.Day;
import entity.DaySummary;
import entity.Player;
import use_case.sleep.SleepDataAccessInterface;

/**
 * Data access object for the sleep use case.
 * Handles persistence and data retrieval for sleep-related operations.
 */
public class SleepDataAccessObject implements SleepDataAccessInterface {
    
    /**
     * Saves the player's current state.
     * In a full implementation, this would persist to a file or database.
     * For now, this is a placeholder that could delegate to SaveFileUserDataObject.
     * 
     * @param player the player to save
     */
    @Override
    public void savePlayerState(Player player) {
        // TODO: Implement actual persistence
        // This could call SaveFileUserDataObject or similar
        System.out.println("Player state saved: " + player.getName() + 
                         " - Day: " + player.getCurrentDay().getDisplayName() +
                         " - Balance: $" + player.getBalance());
    }
    
    /**
     * Creates a day summary from the given information.
     * 
     * @param day the day that was completed
     * @param earnings the total earnings for the day
     * @param spending the total spending for the day
     * @param newBalance the player's new balance
     * @return a DaySummary object
     */
    @Override
    public DaySummary createDaySummary(Day day, double earnings, double spending, double newBalance) {
        return new DaySummary(day, earnings, spending, newBalance);
    }
}