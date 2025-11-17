package interface_adapter.events.sleep;

import interface_adapter.events.ViewManagerModel;
import interface_adapter.events.ViewModel;
import use_case.sleep.SleepInteractor;

/**
 * The presenter for the Sleep Use Case
 */

public class SleepPresenter {
    private final SleepInteractor sleepInteractor;
    private final ViewModel viewModel;
    private final ViewManagerModel viewManagerModel;

    public SleepPresenter(SleepInteractor sleepInteractor, ViewModel viewModel, ViewManagerModel viewManagerModel) {
        this.sleepInteractor = sleepInteractor;
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
    }

    /**
     * Prepare the success view after sleep completes
     */
    @Override
    public void prepareSuccessView(){
        // Get the updated state from the interactor
        int newDay = sleepInteractor.getCurrentDay();

        // Update the view model with the new day
        viewModel.setCurrentDay(newDay);
        viewModel.setSleepSuccess(true);
        viewModel.setNotificationMessage("Slept successfully! Welcome to day " + newDay);

        // Notify the view to update
        viewModel.firePropertyChanged();

    }

    public void prepareFailView(String message){
        viewModel.getSleepSuccess(false);
        viewModel.setErrorMessage(message);
        viewModel.setNotificationMessage("Cannot sleep: " message);

        viewModel.firePropertyChanged();
    }
}
