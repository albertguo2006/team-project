package entity;

import java.util.Objects;

public class EventOutcome {
    private final int outcomeID;
    private final String outcomeName;
    private final String outcomeDescription;
    private final double outcomeChance;
    private final int outcomeResult;

    public EventOutcome(int outcomeID, String outcomeName, String outcomeDescription, double outcomeChance,
                        int outcomeResult) {
        this.outcomeID = outcomeID;
        this.outcomeName = outcomeName;
        this.outcomeDescription = outcomeDescription;
        this.outcomeChance = outcomeChance;
        this.outcomeResult = outcomeResult;
    }
    public String getOutcomeName() {return outcomeName;}
    public int  getOutcomeID() {
        return outcomeID;
    }
    public String getOutcomeDescription() {
        return outcomeDescription;
    }
    public double getOutcomeChance() {
        return outcomeChance;
    }
    public int getOutcomeResult() {
        return outcomeResult;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof EventOutcome)) return false;
        EventOutcome outcome = (EventOutcome) obj;
        return outcomeID == outcome.outcomeID &&
               Double.compare(outcome.outcomeChance, outcomeChance) == 0 &&
               outcomeResult == outcome.outcomeResult &&
               Objects.equals(outcomeName, outcome.outcomeName) &&
               Objects.equals(outcomeDescription, outcome.outcomeDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(outcomeID, outcomeName, outcomeDescription, outcomeChance, outcomeResult);
    }
}
