package interface_adapter.events;

import entity.EventOutcome;
import interface_adapter.ViewManagerModel;
import use_case.events.ActivateRandomOutcome.ActivateRandomOutcomeOutputBoundary;
import use_case.events.ActivateRandomOutcome.ActivateRandomOutcomeOutputData;

import java.util.HashMap;

public class ActivateOutcomePresenter implements ActivateRandomOutcomeOutputBoundary {
    private final EventViewModel eventViewModel;
    private final ViewManagerModel viewManagerModel;

    public ActivateOutcomePresenter(EventViewModel eventViewModel, ViewManagerModel viewManagerModel) {
        this.eventViewModel = eventViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void prepareSuccessView(ActivateRandomOutcomeOutputData activateRandomOutcomeOutputData){
        final EventState eventOutcomeState = eventViewModel.getState();
        String stateName = eventOutcomeState.getName();
        HashMap<Integer, EventOutcome> stateOutcomes =  eventOutcomeState.getOutcomes();
        final EventState newEventOutcomeState = new EventState();
        newEventOutcomeState.setDescription(activateRandomOutcomeOutputData.getDescription());
        newEventOutcomeState.setIndex(activateRandomOutcomeOutputData.getIndex());
        newEventOutcomeState.setName(stateName);
        newEventOutcomeState.setOutcomes(stateOutcomes);
        eventViewModel.setState(newEventOutcomeState);
        eventViewModel.firePropertyChange("Outcome");
    }
}