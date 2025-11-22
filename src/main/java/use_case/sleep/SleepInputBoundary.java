package use_case.sleep;

/**
 * Input boundary for the sleep use case.
 * Defines the interface for initiating sleep.
 */
public interface SleepInputBoundary {
    
    /**
     * Executes the sleep use case.
     *
     * @param inputData the input data containing the player
     */
    void execute(SleepInputData inputData);
}
