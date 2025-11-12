package use_case.events.StartRandomEvent;

import entity.EventOutcome;

import java.util.HashMap;

public class StartRandomEventOutputData {
    private final String eventName;
    private final String eventDescription;
    private final  HashMap<Integer, EventOutcome> outcomes;
    public StartRandomEventOutputData(String eventName, String eventDescription,
                                      HashMap<Integer, EventOutcome> outcomes) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.outcomes = outcomes;
    }
    public String getEventName() {
        return eventName;
    }
    public String getEventDescription() {
        return eventDescription;
    }
    public HashMap<Integer, EventOutcome> getOutcomes() {
        return outcomes;
    }
    public int getOutcomeCount(){
        return outcomes.size();
    }
}
