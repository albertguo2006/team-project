package entity;

public class Event {
    private int eventID;
    private String eventName;
    private String eventDescription;

    public Event(int eventID, String eventName, String eventDescription) {
        this.eventID = eventID;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
    }
}
