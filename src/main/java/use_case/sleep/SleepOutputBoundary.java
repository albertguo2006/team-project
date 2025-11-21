package use_case.sleep;

import entity.GameEnding;

/**
 * Output boundary for the sleep use case.
 * Defines how results are presented back to the user.
 */
public interface SleepOutputBoundary {
    
    /**
     * Presents the day summary to the user.
     *
     * @param outputData the output data containing day summary
     */
    void presentDaySummary(SleepOutputData outputData);
    
    /**
     * Presents the game ending screen.
     *
     * @param ending the game ending information
     */
    void presentGameEnding(GameEnding ending);
    
    /**
     * Presents an error message when sleep fails.
     *
     * @param errorMessage the error message to display
     */
    void presentSleepError(String errorMessage);
}
