package interface_adapter.events.sleep;

import use_case.sleep.SleepInteractor;

/**
 * The controller for the Sleep Use Case
 */

public class SleepController {
    private final SleepInteractor sleepInteractor;

    public SleepController(SleepInteractor sleepInteractor) {
        this.sleepInteractor = sleepInteractor;
    }

    /**
     * Execute the sleep sequence
     */
    public void execute(){
        if (!sleepInteractor.isPlayerInHouse()){
            throw new IllegalStateException("Player must be in house to sleep");
        }
        sleepInteractor.execute();
    }
}
