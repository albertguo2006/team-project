package use_case.events.ActivateRandomOutcome;

import use_case.events.StartRandomEvent.StartRandomEventOutputData;

public interface ActivateRandomOutcomeOutputBoundary {
    public void prepareSuccessView(ActivateRandomOutcomeOutputData activateRandomOutcomeOutputData);
}
