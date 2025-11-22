package use_case.events.StartRandomEvent;

import entity.Event;

public interface StartRandomEventDataAccessInterface {
    Event getEvent(int index);
    int getSize();
}
