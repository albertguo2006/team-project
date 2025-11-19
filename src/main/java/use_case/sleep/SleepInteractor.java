package use_case.sleep;

import entity.Day;
import entity.DaySummary;
import entity.GameEnding;
import entity.Player;

/**
 * Interactor for the sleep use case.
 * Handles the business logic for player sleeping and day advancement.
 */
public class SleepInteractor implements SleepInputBoundary {
    private final SleepOutputBoundary presenter;
    private final SleepDataAccessInterface dataAccess;
    
    /**
     * Constructs a SleepInteractor.
     *
     * @param presenter the output boundary for presenting results
     * @param dataAccess the data access interface for persistence
     */
    public SleepInteractor(SleepOutputBoundary presenter, SleepDataAccessInterface dataAccess) {
        this.presenter = presenter;
        this.dataAccess = dataAccess;
    }
    
    /**
     * Executes the sleep use case.
     * Validates the player can sleep, restores health, creates day summary,
     * and either advances to next day or shows game ending.
     *
     * @param inputData the input data containing the player
     */
    @Override
    public void execute(SleepInputData inputData) {
        Player player = inputData.getPlayer();
        
        // Validate: check if player has already slept today
        if (player.hasSleptToday()) {
            presenter.presentSleepError("You've already slept today. Come back tomorrow!");
            return;
        }
        
        // Get current day before advancing
        Day completedDay = player.getCurrentDay();
        
        // Restore health to 100
        player.setHealth(100);
        
        // Mark that player has slept today
        player.setHasSleptToday(true);
        
        // Create day summary
        DaySummary summary = dataAccess.createDaySummary(
            completedDay,
            player.getDailyEarnings(),
            player.getDailySpending(),
            player.getBalance()
        );
        
        // Check if this was Friday (end of week)
        if (completedDay.isLastDay()) {
            // Week complete - determine ending
            GameEnding ending = GameEnding.determineEnding(player.getBalance());
            
            // Save player state
            dataAccess.savePlayerState(player);
            
            // Present game ending
            presenter.presentGameEnding(ending);
        } else {
            // Not Friday - advance to next day
            boolean advanced = player.advanceDay();
            
            if (advanced) {
                // Reset daily financials for new day
                player.resetDailyFinancials();
                
                // Save player state
                dataAccess.savePlayerState(player);
                
                // Create output data with new day
                SleepOutputData outputData = new SleepOutputData(
                    summary,
                    player.getCurrentDay(),
                    false
                );
                
                // Present day summary
                presenter.presentDaySummary(outputData);
            } else {
                // This shouldn't happen if isLastDay() check worked correctly
                presenter.presentSleepError("Unable to advance to next day.");
            }
        }
    }
}
