package use_case.events.StartRandomEvent;
import entity.Event;
import entity.EventOutcome;

import java.util.HashMap;
import java.util.Random;

public class StartRandomEventInteractor implements StartRandomEventInputBoundary {
    private final Random random = new Random();
    private final StartRandomEventDataAccessInterface startRandomEventDataAccessObject;
    private final StartRandomEventOutputBoundary startRandomEventPresenter;
    private int eventPity = 0;

    public StartRandomEventInteractor(StartRandomEventDataAccessInterface startRandomEventDataAccessObject,
                                      StartRandomEventOutputBoundary startRandomEventOutputBoundary){
        this.startRandomEventDataAccessObject = startRandomEventDataAccessObject;
        this.startRandomEventPresenter = startRandomEventOutputBoundary;
    }

    public void execute(){
        int randInt =  random.nextInt(101);
        if (eventPity >= randInt){
            eventPity = 0;
            int randomIndex = random.nextInt(startRandomEventDataAccessObject.getSize());
            Event selectedEvent = startRandomEventDataAccessObject.getEvent(randomIndex);
            StartRandomEventOutputData reod = new StartRandomEventOutputData(selectedEvent);
        }
        else{
            eventPity += 25;
        }
    }

}




