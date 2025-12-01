package interface_adapter.events;

import entity.EventOutcome;
import use_case.events.ActivateRandomOutcome.ActivateRandomOutcomeInputBoundary;
import use_case.events.ActivateRandomOutcome.ActivateRandomOutcomeInputData;

import java.util.HashMap;

public class EventOutcomeController{
    private final ActivateRandomOutcomeInputBoundary activateRandomOutcomeInteractor;

    public EventOutcomeController(ActivateRandomOutcomeInputBoundary activateRandomOutcomeInteractor) {
        this.activateRandomOutcomeInteractor = activateRandomOutcomeInteractor;
    }

    public void execute(HashMap<Integer, EventOutcome> outcomes){
        ActivateRandomOutcomeInputData  input = new ActivateRandomOutcomeInputData(outcomes);
        activateRandomOutcomeInteractor.execute(input);
    }
}
