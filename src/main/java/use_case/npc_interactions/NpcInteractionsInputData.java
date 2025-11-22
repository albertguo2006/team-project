package use_case.npc_interactions;

public class NpcInteractionsInputData {
    private final String npcName;
    private final String userMessage;

    public NpcInteractionsInputData(String npcName, String userMessage) {
        this.npcName = npcName;
        this.userMessage = userMessage;
    }

    public String getNpcName() { return npcName; }
    public String getUserMessage() { return userMessage; }
}