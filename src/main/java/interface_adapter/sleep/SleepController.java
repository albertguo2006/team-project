package interface_adapter.sleep;

import entity.Player;
import use_case.sleep.SleepInputBoundary;
import use_case.sleep.SleepInputData;

/**
 * Controller for the sleep use case.
 * Translates user actions into use case inputs.
 */
public class SleepController {
    private final SleepInputBoundary sleepInteractor;
    
    /**
     * Constructs a SleepController.
     * 
     * @param sleepInteractor the sleep input boundary (interactor)
     */
    public SleepController(SleepInputBoundary sleepInteractor) {
        this.sleepInteractor = sleepInteractor;
    }
    
    /**
     * Initiates the sleep action for the player.
     * 
     * @param player the player who wants to sleep
     */
    public void sleep(Player player) {
        SleepInputData inputData = new SleepInputData(player);
        sleepInteractor.execute(inputData);
    }
}