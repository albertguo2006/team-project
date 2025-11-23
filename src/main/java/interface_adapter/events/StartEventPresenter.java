package interface_adapter.events;

import interface_adapter.events.ViewManagerModel;
import use_case.events.StartRandomEvent.StartRandomEventOutputBoundary;
import use_case.events.StartRandomEvent.StartRandomEventOutputData;

public class StartEventPresenter implements StartRandomEventOutputBoundary{
    private final EventViewModel eventViewModel;
    private final ViewManagerModel viewManagerModel;

    public StartEventPresenter(EventViewModel eventViewModel, ViewManagerModel viewManagerModel) {
        this.eventViewModel = eventViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void prepareSuccessView(StartRandomEventOutputData startRandomEventOutputData){

        final EventState eventState = new EventState();
        eventState.setName(startRandomEventOutputData.getEventName());
        eventState.setDescription(startRandomEventOutputData.getEventDescription());
        eventState.setOutcomes(startRandomEventOutputData.getOutcomes());
        eventViewModel.setState(eventState);
        eventViewModel.firePropertyChange("Event");

        this.viewManagerModel.setState(eventViewModel.getViewName());
        this.viewManagerModel.firePropertyChange();
    }
}
