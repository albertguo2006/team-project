package entity;

import java.util.HashMap;
import java.util.Objects;

public class Event {
    private final int eventID;
    private final String eventName;
    private final String eventDescription;
    private final HashMap<Integer, EventOutcome> outcomes;

    public Event(int eventID, String eventName, String eventDescription, HashMap<Integer, EventOutcome> outcomes) {
        this.eventID = eventID;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.outcomes = outcomes;
    }

    public int getEventID() {
        return eventID;
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

    @Override
    public boolean equals(Object obj){
        if (this == obj){
            return true;
        }
        if (!(obj instanceof Event)){
            return false;
        }
        Event event = (Event) obj;
        return this.eventID == event.getEventID(); }

    @Override
    public int hashCode() {
        return Objects.hash(eventID);
    }

}


