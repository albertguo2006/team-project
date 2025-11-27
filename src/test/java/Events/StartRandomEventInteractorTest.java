package Events;

import data_access.EventDataAccessObject;
import org.junit.Test;
import use_case.events.StartRandomEvent.StartRandomEventInputBoundary;
import use_case.events.StartRandomEvent.StartRandomEventInteractor;
import use_case.events.StartRandomEvent.StartRandomEventOutputBoundary;
import use_case.events.StartRandomEvent.StartRandomEventOutputData;
import static org.junit.jupiter.api.Assertions.*;

public class StartRandomEventInteractorTest {

    @Test
    public void successStartRandomEventTest() {
        EventDataAccessObject eventDataAccessObject = new EventDataAccessObject();
        eventDataAccessObject.createEventList();

        // Success Presenter
        StartRandomEventOutputBoundary startRandomEventPresenter = new StartRandomEventOutputBoundary() {
            @Override
            public void prepareSuccessView(StartRandomEventOutputData startRandomEventOutputData) {
                // Test that the random event chosen was one of the events in the DAO
                assertTrue(eventDataAccessObject.getEventList().contains(startRandomEventOutputData.getEvent()));
            }
        };

        // Manually set eventPity to guarentee an event is chosen
        StartRandomEventInputBoundary startRandomEventInteractor = new StartRandomEventInteractor(eventDataAccessObject,
                startRandomEventPresenter, 200);
        startRandomEventInteractor.execute();
    }
}
