package interface_adapter.events;

import entity.Player;
import use_case.events.StartRandomEvent.StartRandomEventInputBoundary;

public class StartEventController {
    private final StartRandomEventInputBoundary startRandomEventInteractor;

    public StartEventController(StartRandomEventInputBoundary startRandomEventInteractor) {
        this.startRandomEventInteractor = startRandomEventInteractor;
    }
    public void execute(){
        startRandomEventInteractor.execute();
    }
}
