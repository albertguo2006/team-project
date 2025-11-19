package entity;

public class NPC {
    private final String name;
    private String dialoguePrompt;
    private String location;
    private double cashBalance;
    private final int defaultScore;

    public NPC(String name, String dialoguePrompt, String location, double cashBalance, int defaultScore) {
        this.name = name;
        this.dialoguePrompt = dialoguePrompt;
        this.location = location;
        this.cashBalance = cashBalance;
        this.defaultScore = defaultScore;
    }

    public String getName() {
        return name;
    }

    public void setDialoguePrompt(String dialoguePrompt) {
        this.dialoguePrompt = dialoguePrompt;
    }

    public String getDialoguePrompt() {
        return dialoguePrompt;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setCashBalance(double cashBalance) {
        this.cashBalance = cashBalance;
    }

    public double getCashBalance() {
        return cashBalance;
    }

    public int getDefaultScore() {
        return defaultScore;
    }
    // added a default score just in case certain NPCs start out with different default relationship
    // scores (for example, some NPCs may start out friendlier than others, thus having higher starting scores).
    // this can be deleted if we decide against this.
}
