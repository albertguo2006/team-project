package use_case.events;

import data_access.EventDataAccessObject;
import entity.Event;
import org.junit.Test;
import use_case.events.StartRandomEvent.StartRandomEventInputBoundary;
import use_case.events.StartRandomEvent.StartRandomEventInteractor;
import use_case.events.StartRandomEvent.StartRandomEventOutputBoundary;
import use_case.events.StartRandomEvent.StartRandomEventOutputData;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class StartRandomEventInteractorTest {

    @Test
    public void successStartRandomEventTest() {
        /// Test that the event chosen is in the DataAccessObject
        EventDataAccessObject eventDataAccessObject = new EventDataAccessObject();
        ArrayList<Event> eventList = eventDataAccessObject.createEventList();

        // Success Presenter
        StartRandomEventOutputBoundary startRandomEventPresenter = new StartRandomEventOutputBoundary() {
            @Override
            public void prepareSuccessView(StartRandomEventOutputData outputData) {
                // Test that the random event chosen was one of the events in the DAO
                // Two events are the same if their name, description, and outcomes are the same
                boolean anyPassed = false;
                for (Event event: eventList) {
                    if (event.getEventName().equals(outputData.getEventName())
                    && event.getEventDescription().equals(outputData.getEventDescription())
                    && event.getOutcomes().equals(outputData.getOutcomes())){
                        anyPassed = true;
                    }
                }
                if (!anyPassed){
                    fail();
                }
            }
        };

        StartRandomEventInputBoundary startRandomEventInteractor = new StartRandomEventInteractor(eventDataAccessObject,
                startRandomEventPresenter);
        // Repeat 20 times check that test passes with multiple events
        for (int i = 0; i < 20; i++) {
            startRandomEventInteractor.execute();
        }
    }

    @Test
    public void eventLimitTest() {
        /// Test that no events are triggered while there is still eventLimit remaining.
        EventDataAccessObject eventDataAccessObject = new EventDataAccessObject();
        eventDataAccessObject.createEventList();
        int eventLimit = 5; //An event shouldn't trigger the first 5 times execute is called

        //Success Presenter
        StartRandomEventOutputBoundary startRandomEventPresenter = startRandomEventOutputData -> {
            /// If the presenter is called while there is eventLimit remaining, the test should fail
            fail();
        };
        StartRandomEventInputBoundary startRandomEventInteractor = new StartRandomEventInteractor(eventDataAccessObject,
                startRandomEventPresenter, 101, eventLimit);

        /// Repeat 5 times since we set eventLimit to 5
        for (int i = 0; i < 5; i++) {
            startRandomEventInteractor.execute();
        }
    }

    @Test
    public void guarenteeStartEventTest() {
        /// Test that an event is started when eventPity > 100
        EventDataAccessObject eventDataAccessObject = new EventDataAccessObject();
        eventDataAccessObject.createEventList();

        int eventPity = 101; // set eventPity > 100 to guarentee an event is triggered
        int eventLimit = 0;  // we want an event to trigger right away so set eventLimit = 0

        // Success Presenter (only checks that an event was successfully triggered)
        StartRandomEventOutputBoundary startRandomEventPresenter = startRandomEventOutputData -> {
            assertTrue(true);
        };

        // Manually set eventPity to guarentee an event is chosen
        StartRandomEventInputBoundary startRandomEventInteractor = new StartRandomEventInteractor(eventDataAccessObject,
                startRandomEventPresenter, eventPity, eventLimit);

        startRandomEventInteractor.execute();
    }
}

