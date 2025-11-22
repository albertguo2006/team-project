package interface_adapter.sleep;

import entity.GameEnding;
import interface_adapter.events.ViewManagerModel;
import use_case.sleep.SleepOutputBoundary;
import use_case.sleep.SleepOutputData;

/**
 * Presenter for the sleep use case.
 * Translates use case output into view model updates.
 */
public class SleepPresenter implements SleepOutputBoundary {
    private final ViewManagerModel viewManagerModel;
    private final SleepViewModel sleepViewModel;
    
    /**
     * Constructs a SleepPresenter.
     * 
     * @param viewManagerModel the view manager for switching views
     * @param sleepViewModel the sleep view model to update
     */
    public SleepPresenter(ViewManagerModel viewManagerModel, SleepViewModel sleepViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.sleepViewModel = sleepViewModel;
    }
    
    /**
     * Presents the day summary to the user.
     * Updates the view model and switches to the day summary view.
     * 
     * @param outputData the output data containing day summary
     */
    @Override
    public void presentDaySummary(SleepOutputData outputData) {
        // Update view model with summary data
        sleepViewModel.setCurrentSummary(outputData.getSummary());
        sleepViewModel.setNewDay(outputData.getNewDay());
        sleepViewModel.setWeekComplete(outputData.isWeekComplete());
        sleepViewModel.setErrorMessage(null);
        
        // Switch to day summary view
        viewManagerModel.setState("daySummary");
        viewManagerModel.firePropertyChange();
    }
    
    /**
     * Presents the game ending screen.
     * Updates the view model and switches to the end game view.
     * 
     * @param ending the game ending information
     */
    @Override
    public void presentGameEnding(GameEnding ending) {
        // Update view model with ending data
        sleepViewModel.setEnding(ending);
        sleepViewModel.setWeekComplete(true);
        sleepViewModel.setErrorMessage(null);
        
        // Switch to end game view
        viewManagerModel.setState("endGame");
        viewManagerModel.firePropertyChange();
    }
    
    /**
     * Presents an error message when sleep fails.
     * 
     * @param errorMessage the error message to display
     */
    @Override
    public void presentSleepError(String errorMessage) {
        // Update view model with error
        sleepViewModel.setErrorMessage(errorMessage);
        
        // Don't switch views, just update the current view to show error
        // The GamePanel can display this as a toast/notification
    }
}