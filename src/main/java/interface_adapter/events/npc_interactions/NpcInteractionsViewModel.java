package interface_adapter.events.npc_interactions;

public class NpcInteractionsViewModel {

    private String npcName;
    private String aiResponse;

    private Runnable listener;

    public void setNpcName(String npcName) { this.npcName = npcName; }
    public void setAiResponse(String aiResponse) { this.aiResponse = aiResponse; }

    public String getNpcName() { return npcName; }
    public String getAiResponse() { return aiResponse; }

    public void setListener(Runnable listener) {
        this.listener = listener;
    }

    public void fireUpdate() {
        if (listener != null) listener.run();
    }
}