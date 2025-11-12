package entity;

public class EventOutcome {
    private final int outcomeID;
    private final String outcomeDescription;
    private final double outcomeChance;
    private final int outcomeResult;

    public EventOutcome(int outcomeID, String outcomeDescription, double outcomeChance,
                        int outcomeResult) {
        this.outcomeID = outcomeID;
        this.outcomeDescription = outcomeDescription;
        this.outcomeChance = outcomeChance;
        this.outcomeResult = outcomeResult;
    }
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
}
