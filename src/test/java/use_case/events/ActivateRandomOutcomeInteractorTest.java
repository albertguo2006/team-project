package use_case.events;

import data_access.EventDataAccessObject;
import entity.Event;
import entity.Player;
import org.junit.Test;
import use_case.events.ActivateRandomOutcome.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class ActivateRandomOutcomeInteractorTest {
    double oldBalance;

    @Test
    public void successActivateRandomOutcomeTest(){
        String source = "src/test/resources/events/test_events.json";
        EventDataAccessObject eventDataAccessObject = new EventDataAccessObject();
        /// select a single event for testing
        Event testEvent = eventDataAccessObject.createEventList(source).get(0);

        Player player = new Player("Test Player");
        oldBalance = player.getBalance();

        eventDataAccessObject.setPlayer(player);

        ActivateRandomOutcomeInputData inputData = new ActivateRandomOutcomeInputData(testEvent.getOutcomes());

        ActivateRandomOutcomeOutputBoundary activateRandomOutcomePresenter = new ActivateRandomOutcomeOutputBoundary() {
            @Override
            public void prepareSuccessView(ActivateRandomOutcomeOutputData activateRandomOutcomeOutputData) {
                //Test that the random outcome is one of the events from the input Event
                // (check if name and description match)
                assertSame(inputData.getOutcomes().get(activateRandomOutcomeOutputData.getIndex()).getOutcomeName(),
                        activateRandomOutcomeOutputData.getName());
                assertSame(inputData.getOutcomes().get(activateRandomOutcomeOutputData.getIndex()).
                                getOutcomeDescription(), activateRandomOutcomeOutputData.getDescription());
                //Test that the player's current balance == the players old balance +
                // whatever value was stored in the outcome
                assertEquals(player.getBalance(),
                        oldBalance + activateRandomOutcomeOutputData.getResult());
            }

        };
        ActivateRandomOutcomeInputBoundary activateRandomOutcomeInteractor = new ActivateRandomOutcomeInteractor(
                eventDataAccessObject, activateRandomOutcomePresenter);

        /// Repeat 50 times since the outcome is random
        for (int i = 0; i < 50; i++) {
            activateRandomOutcomeInteractor.execute(inputData);
            oldBalance = player.getBalance();
        }
    }
}
