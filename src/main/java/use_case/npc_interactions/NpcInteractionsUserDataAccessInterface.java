// still needs fix (commented out to fix code)

package use_case.npc_interactions;

import entity.NPC;

import java.util.HashMap;

public interface NpcInteractionsUserDataAccessInterface {

    HashMap<String, NPC> getNPCMap();

    // temporary here
    NPC getNpcByName(String name);
    void saveNpc(NPC npc);
}

