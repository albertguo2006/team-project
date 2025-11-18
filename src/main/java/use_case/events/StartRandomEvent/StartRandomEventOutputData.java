package use_case.events.StartRandomEvent;

import entity.Event;
import entity.EventOutcome;

import java.util.HashMap;

public class StartRandomEventOutputData {
    private final Event event;
    public StartRandomEventOutputData(Event event) {
        this.event = event;
    }
    public String getEventName() {return event.getEventName();}
    public String getEventDescription() {
        return event.getEventDescription();
    }
    public HashMap<Integer, EventOutcome> getOutcomes() {
        return event.getOutcomes();
    }
    public int getOutcomeCount(){
        return event.getOutcomes().size();
    }
}
