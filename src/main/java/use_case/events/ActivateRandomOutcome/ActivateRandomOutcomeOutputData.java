package use_case.events.ActivateRandomOutcome;

import entity.EventOutcome;

public class ActivateRandomOutcomeOutputData{
    EventOutcome outcome;
    int index;
    public ActivateRandomOutcomeOutputData(EventOutcome outcome, int index) {
        this.outcome = outcome;
        this.index = index;
    }
    public String getName(){
        return outcome.getOutcomeName();
    }
    public String getDescription(){
        return outcome.getOutcomeDescription();
    }
    public double getResult() {return outcome.getOutcomeResult();}
    public int getIndex(){
        return index;
    }
//    public EventOutcome getOutcome(){
//        return outcome;
//    }
}
