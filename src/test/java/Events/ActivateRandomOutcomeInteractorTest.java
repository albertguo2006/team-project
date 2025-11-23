package Events;

import data_access.EventDataAccessObject;
import entity.Event;
import entity.Player;
import org.junit.Test;
import use_case.events.ActivateRandomOutcome.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class ActivateRandomOutcomeInteractorTest {

    @Test
    public void successActivateRandomOutcomeTest(){
        EventDataAccessObject eventDataAccessObject = new EventDataAccessObject();
        eventDataAccessObject.createEventList();

        Player player = new Player("Test Player");
        final double startingBalance = player.getBalance();

        eventDataAccessObject.setPlayer(player);

        Event testEvent = eventDataAccessObject.getEventList().get(0);

        ActivateRandomOutcomeInputData inputData = new ActivateRandomOutcomeInputData(testEvent.getOutcomes());

        ActivateRandomOutcomeOutputBoundary activateRandomOutcomePresenter = new ActivateRandomOutcomeOutputBoundary() {
            @Override
            public void prepareSuccessView(ActivateRandomOutcomeOutputData activateRandomOutcomeOutputData) {
                //Test that the random outcome is one of the events from the input Event
                assertSame(inputData.getOutcomes().get(activateRandomOutcomeOutputData.getIndex()),
                        activateRandomOutcomeOutputData.getOutcome());
                //Test that the player's current balance == the players starting balance
                // + whatever value was stored in the outcome
                assertEquals(player.getBalance(),
                        startingBalance + activateRandomOutcomeOutputData.getOutcome().getOutcomeResult());

            }

        };
        ActivateRandomOutcomeInputBoundary activateRandomOutcomeInteractor = new ActivateRandomOutcomeInteractor(
                eventDataAccessObject, activateRandomOutcomePresenter);
        activateRandomOutcomeInteractor.execute(inputData);
    }
}
