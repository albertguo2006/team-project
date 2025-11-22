package interface_adapter.sleep;

import entity.Day;
import entity.DaySummary;
import entity.GameEnding;
import interface_adapter.events.ViewModel;

/**
 * View model for the sleep system.
 * Holds the state for day summary and game ending screens.
 */
public class SleepViewModel extends ViewModel {
    private DaySummary currentSummary;
    private Day newDay;
    private boolean isWeekComplete;
    private GameEnding ending;
    private String errorMessage;
    
    /**
     * Constructs a SleepViewModel.
     */
    public SleepViewModel() {
        super("sleep");
    }
    
    /**
     * Gets the current day summary.
     * @return the DaySummary
     */
    public DaySummary getCurrentSummary() {
        return currentSummary;
    }
    
    /**
     * Sets the current day summary.
     * @param summary the DaySummary to set
     */
    public void setCurrentSummary(DaySummary summary) {
        this.currentSummary = summary;
        firePropertyChange();
    }
    
    /**
     * Gets the new day after sleeping.
     * @return the new Day
     */
    public Day getNewDay() {
        return newDay;
    }
    
    /**
     * Sets the new day.
     * @param day the Day to set
     */
    public void setNewDay(Day day) {
        this.newDay = day;
        firePropertyChange();
    }
    
    /**
     * Checks if the week is complete.
     * @return true if week complete, false otherwise
     */
    public boolean isWeekComplete() {
        return isWeekComplete;
    }
    
    /**
     * Sets whether the week is complete.
     * @param weekComplete true if week complete
     */
    public void setWeekComplete(boolean weekComplete) {
        this.isWeekComplete = weekComplete;
        firePropertyChange();
    }
    
    /**
     * Gets the game ending.
     * @return the GameEnding
     */
    public GameEnding getEnding() {
        return ending;
    }
    
    /**
     * Sets the game ending.
     * @param ending the GameEnding to set
     */
    public void setEnding(GameEnding ending) {
        this.ending = ending;
        firePropertyChange();
    }
    
    /**
     * Gets the error message.
     * @return the error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }
    
    /**
     * Sets the error message.
     * @param message the error message to set
     */
    public void setErrorMessage(String message) {
        this.errorMessage = message;
        firePropertyChange();
    }
}