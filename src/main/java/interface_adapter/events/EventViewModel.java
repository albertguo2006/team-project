package interface_adapter.events;

import interface_adapter.events.ViewModel;

public class EventViewModel extends ViewModel<EventState> {

    public EventViewModel(String viewName) {
        super(viewName);
        setState(new EventState());
    }
}
