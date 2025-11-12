package interface_adapter.events;

import entity.Event;
import use_case.events.StartRandomEvent.StartRandomEventInputBoundary;
import use_case.events.StartRandomEvent.StartRandomEventInputData;

import java.util.ArrayList;
import java.util.Random;

public class EventController {
    private final Random random = new Random();
    private final StartRandomEventInputBoundary startRandomEventInputBoundary;
    private int eventPity = 0;

    public EventController(StartRandomEventInputBoundary startRandomEventInputBoundary) {
        this.startRandomEventInputBoundary = startRandomEventInputBoundary;
    }

    public void attemptRandomEvent(ArrayList<Event> events){
        int randInt =  random.nextInt(1,101);
        if (eventPity >= randInt){
            StartRandomEventInputData startRandomEventInputData = new StartRandomEventInputData(events);
            startRandomEventInputBoundary.selectRandomEvent(startRandomEventInputData);
            eventPity = 0;
        }
        else{
            eventPity += 25;
        }
    }
}
