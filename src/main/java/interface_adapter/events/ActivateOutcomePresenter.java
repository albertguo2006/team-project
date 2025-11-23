package interface_adapter.events;

import use_case.events.ActivateRandomOutcome.ActivateRandomOutcomeOutputBoundary;
import use_case.events.ActivateRandomOutcome.ActivateRandomOutcomeOutputData;

public class ActivateOutcomePresenter implements ActivateRandomOutcomeOutputBoundary {
    private final EventViewModel eventViewModel;
    public ActivateOutcomePresenter(EventViewModel eventViewModel) {
        this.eventViewModel = eventViewModel;
    }

    @Override
    public void prepareSuccessView(ActivateRandomOutcomeOutputData activateRandomOutcomeOutputData){
        final EventState eventOutcomeState = eventViewModel.getState();
        eventOutcomeState.setDescription(activateRandomOutcomeOutputData.getDescription());
        eventOutcomeState.setIndex(activateRandomOutcomeOutputData.getIndex());
        eventViewModel.setState(eventOutcomeState);
        eventViewModel.firePropertyChange("Outcome");
    }
}