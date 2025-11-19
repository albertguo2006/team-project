package use_case.npc_interactions;

import entity.NPC;

public interface NpcInteractionsUserDataAccessInterface {
    NPC getNpcByName(String name);
    void saveNpc(NPC npc);
}
