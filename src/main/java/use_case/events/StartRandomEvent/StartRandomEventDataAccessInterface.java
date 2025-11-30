package use_case.events.StartRandomEvent;

import entity.Event;
import entity.Player;

import java.util.ArrayList;

public interface StartRandomEventDataAccessInterface {
    Event getEvent(int index);
    int getSize();
    ArrayList<Event> createEventList();
    ArrayList<Event> getEventList();
    void setPlayer(Player player);
}
