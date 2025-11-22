package use_case.npc_interactions;

public class NpcInteractionsOutputData {
    private final String npcName;
    private final String aiResponse;

    public NpcInteractionsOutputData(String npcName, String aiResponse) {
        this.npcName = npcName;
        this.aiResponse = aiResponse;
    }

    public String getNpcName() { return npcName; }
    public String getAiResponse() { return aiResponse; }
}