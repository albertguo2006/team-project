package use_case.events.ActivateRandomOutcome;

import entity.EventOutcome;

import java.util.HashMap;

public class ActivateRandomOutcomeInputData {
    private final HashMap<Integer, EventOutcome> outcomes;
    private final int selectedOutcomeIndex;  // -1 means random selection

    public ActivateRandomOutcomeInputData(HashMap<Integer, EventOutcome> outcomes){
        this.outcomes = outcomes;
        this.selectedOutcomeIndex = -1;  // Default to random
    }

    public ActivateRandomOutcomeInputData(HashMap<Integer, EventOutcome> outcomes, int selectedOutcomeIndex){
        this.outcomes = outcomes;
        this.selectedOutcomeIndex = selectedOutcomeIndex;
    }

    public HashMap<Integer, EventOutcome> getOutcomes() {
        return this.outcomes;
    }

    public int getSelectedOutcomeIndex() {
        return this.selectedOutcomeIndex;
    }

    public boolean hasSelectedOutcome() {
        return this.selectedOutcomeIndex >= 0;
    }
}
