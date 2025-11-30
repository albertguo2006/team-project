package entity;

import java.util.HashMap;
import java.util.Objects;

public class Event {
    private final int eventID;
    private final String eventName;
    private final String eventDescription;
    private final HashMap<Integer, EventOutcome> outcomes;
    private final double probability;

    public Event(int eventID, String eventName, String eventDescription, HashMap<Integer, EventOutcome> outcomes, double probability) {
        this.eventID = eventID;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.outcomes = outcomes;
        this.probability = probability;
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

    public double getProbability() {
        return probability;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Event)) return false;
        Event event = (Event) obj;
        return eventID == event.eventID &&
               Double.compare(event.probability, probability) == 0 &&
               Objects.equals(eventName, event.eventName) &&
               Objects.equals(eventDescription, event.eventDescription) &&
               Objects.equals(outcomes, event.outcomes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventID, eventName, eventDescription, outcomes, probability);
    }

}


