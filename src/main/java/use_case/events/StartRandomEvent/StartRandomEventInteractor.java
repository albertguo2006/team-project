package use_case.events.StartRandomEvent;
import entity.Event;

import java.util.Random;

public class StartRandomEventInteractor implements StartRandomEventInputBoundary {
    private final Random random = new Random();
    private final StartRandomEventDataAccessInterface startRandomEventDataAccessObject;
    private final StartRandomEventOutputBoundary startRandomEventPresenter;
    private int eventPity;
    private int eventLimit = 3; // # rooms that must be traversed before event pity starts increasing

    public StartRandomEventInteractor(StartRandomEventDataAccessInterface startRandomEventDataAccessObject,
                                      StartRandomEventOutputBoundary startRandomEventOutputBoundary){
        this.startRandomEventDataAccessObject = startRandomEventDataAccessObject;
        this.startRandomEventPresenter = startRandomEventOutputBoundary;
        this.eventPity = 0; //Chance to start a random event. Defaults to 0 and increases by 5% every time an event
                            // is not triggeredd
    }

    /// Can manually set pity for testing purposes
    public StartRandomEventInteractor(StartRandomEventDataAccessInterface startRandomEventDataAccessObject,
                                      StartRandomEventOutputBoundary startRandomEventOutputBoundary, int eventPity,
                                      int eventLimit){
        this.startRandomEventDataAccessObject = startRandomEventDataAccessObject;
        this.startRandomEventPresenter = startRandomEventOutputBoundary;
        this.eventPity = eventPity;
        this.eventLimit = eventLimit;
    }

    public void execute(){
        int randInt =  random.nextInt(101);
        if (eventPity >= randInt && eventLimit == 0) {
            eventPity = 0;
            eventLimit = 4;
            /// Selects a random event
            int randomIndex = random.nextInt(startRandomEventDataAccessObject.getSize());
            Event selectedEvent = startRandomEventDataAccessObject.getEvent(randomIndex);
            StartRandomEventOutputData reod = new StartRandomEventOutputData(selectedEvent);
            startRandomEventPresenter.prepareSuccessView(reod);
        }
        else{
            eventPity += 10;
            if (eventLimit > 0){
                eventLimit -= 1;
            }
        }
    }
}




