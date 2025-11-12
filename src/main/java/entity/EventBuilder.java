package entity;

import java.util.HashMap;

public class EventBuilder {
    public Event createEvent(int eventID, String eventName, String eventDescription,
                             HashMap<Integer, EventOutcome> outcomes) {
        return new Event(eventID, eventName, eventDescription, outcomes);
    }
    public EventOutcome createOutcome(int outcomeID, String outcomeDescription,
                                      double outcomeChance, int outcomeResult){
        return new EventOutcome(outcomeID, outcomeDescription, outcomeChance, outcomeResult);
    }
}
