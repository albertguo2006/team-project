package use_case.sleep;

import entity.Day;
import entity.DaySummary;

/**
 * Output data from the sleep use case.
 * Contains the day summary and information about whether the week is complete.
 */
public class SleepOutputData {
    private final DaySummary summary;
    private final Day newDay;
    private final boolean isWeekComplete;
    
    /**
     * Constructs SleepOutputData.
     * 
     * @param summary the summary of the completed day
     * @param newDay the new current day (null if week is complete)
     * @param isWeekComplete true if the week has ended (Friday complete)
     */
    public SleepOutputData(DaySummary summary, Day newDay, boolean isWeekComplete) {
        this.summary = summary;
        this.newDay = newDay;
        this.isWeekComplete = isWeekComplete;
    }
    
    /**
     * Gets the day summary.
     * @return the DaySummary
     */
    public DaySummary getSummary() {
        return summary;
    }
    
    /**
     * Gets the new current day.
     * @return the new Day (null if week complete)
     */
    public Day getNewDay() {
        return newDay;
    }
    
    /**
     * Checks if the week is complete.
     * @return true if week complete (slept on Friday), false otherwise
     */
    public boolean isWeekComplete() {
        return isWeekComplete;
    }
}
