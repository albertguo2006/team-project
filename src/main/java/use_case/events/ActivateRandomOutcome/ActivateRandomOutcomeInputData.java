package use_case.events.ActivateRandomOutcome;

import entity.EventOutcome;

import java.util.HashMap;

public class ActivateRandomOutcomeInputData {
    private final HashMap<Integer, EventOutcome> outcomes;
    public ActivateRandomOutcomeInputData(HashMap<Integer, EventOutcome> outcomes){
        this.outcomes = outcomes;
    }
    public HashMap<Integer, EventOutcome> getOutcomes() {
        return this.outcomes;
    }
}
