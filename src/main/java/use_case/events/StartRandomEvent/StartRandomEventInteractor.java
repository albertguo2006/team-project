package use_case.events.StartRandomEvent;
import entity.Event;
import entity.EventOutcome;

import java.util.HashMap;
import java.util.Random;

public class StartRandomEventInteractor implements StartRandomEventInputBoundary {
    private final Random random = new Random();
    private final StartRandomEventDataAccessInterface startRandomEventDataAccessObject;
    private final StartRandomEventOutputBoundary startRandomEventPresenter;
    private int eventPity;

    public StartRandomEventInteractor(StartRandomEventDataAccessInterface startRandomEventDataAccessObject,
                                      StartRandomEventOutputBoundary startRandomEventOutputBoundary){
        this.startRandomEventDataAccessObject = startRandomEventDataAccessObject;
        this.startRandomEventPresenter = startRandomEventOutputBoundary;
        this.eventPity = 0; //Chance to start a random event. Defaults to 0 and increases by 25% every time an event
                            // is not triggered.
    }

    /// Can manually set pity for testing purposes
    public StartRandomEventInteractor(StartRandomEventDataAccessInterface startRandomEventDataAccessObject,
                                      StartRandomEventOutputBoundary startRandomEventOutputBoundary, int eventPity){
        this.startRandomEventDataAccessObject = startRandomEventDataAccessObject;
        this.startRandomEventPresenter = startRandomEventOutputBoundary;
        this.eventPity = eventPity;
    }

    public void execute(){
        int randInt =  random.nextInt(101);
        if (eventPity >= randInt){
            eventPity = 0;
            /// Selects a random event from the DAO and passes to the presenter
            int randomIndex = random.nextInt(startRandomEventDataAccessObject.getSize());
            Event selectedEvent = startRandomEventDataAccessObject.getEvent(randomIndex);
            StartRandomEventOutputData reod = new StartRandomEventOutputData(selectedEvent);
            startRandomEventPresenter.prepareSuccessView(reod);
        }
        else{
            eventPity += 25;
            /// somehow needs to communicate with whatever called the controller that an event was not triggered.
        }
    }

}




