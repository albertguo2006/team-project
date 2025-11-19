// still needs to fix (reverted so code wont crash)

package data_access;

import entity.NPC;
import use_case.npc_interactions.NpcInteractionsUserDataAccessInterface;

import java.util.HashMap;
import java.util.Map;

public class NpcInteractionsDataAccess implements NpcInteractionsUserDataAccessInterface {

    private final Map<String, NPC> npcs = new HashMap<>();

    public NpcInteractionsDataAccess() {
        npcs.put("Bob", new NPC(
                "Bob",
                "Hello, I'm Bob.",
                "Subway",
                50.0,
                10
        ));
    }

    @Override
    public NPC getNpcByName(String name) {
        return npcs.get(name);
    }

    @Override
    public void saveNpc(NPC npc) {
        npcs.put(npc.getName(), npc);
    }

    // temporary
    @Override
    public HashMap<String, NPC> getNPCMap(){
        return new HashMap<>(npcs);
    }
}