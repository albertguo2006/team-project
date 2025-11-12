package use_case.events.StartRandomEvent;
import entity.Event;
import entity.EventOutcome;

import java.util.HashMap;
import java.util.Random;

public class StartRandomEventInteractor implements StartRandomEventInputBoundary {
    Random random = new Random();

    public void selectRandomEvent(StartRandomEventInputData events) {
        int r = random.nextInt(events.getSize());
        Event selectedEvent = events.getEvent(r);
        String eventName = selectedEvent.getEventName();
        String eventDescription = selectedEvent.getEventDescription();
        HashMap<Integer, EventOutcome> outcomes = selectedEvent.getOutcomes();

        StartRandomEventOutputData reod = new StartRandomEventOutputData(eventName, eventDescription, outcomes);
/// To be implemented
    }

}




