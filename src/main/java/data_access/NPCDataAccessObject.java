// still needs to fix (temp commented out)


package data_access;

import entity.NPC;
import org.json.JSONArray;
import org.json.JSONObject;
import use_case.npc_interactions.NpcInteractionsUserDataAccessInterface;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


import static data_access.LoadFileUserDataAccessObject.JSONFileReader;


public class NPCDataAccessObject implements NpcInteractionsUserDataAccessInterface {
    private Map<String, NPC> allNpcs;

    public NPCDataAccessObject() {
        this.allNpcs = loadNpcsFromJson("src/main/resources/npc_prompts.json");
    }

    @Override
    public Map<String, NPC> getAllNpcs() {
        return allNpcs;
    }

    public NPC getRandomNpc() {
        if (allNpcs.isEmpty()) return null;
        Object[] values = allNpcs.values().toArray();
        return (NPC) values[new Random().nextInt(values.length)];
    }

    private Map<String, NPC> loadNpcsFromJson(String filePath) {
        Map<String, NPC> npcMap = new HashMap<>();
        try {
            String content = Files.readString(Paths.get(filePath));
            JSONArray npcArray = new JSONArray(content);

            for (int i = 0; i < npcArray.length(); i++) {
                JSONObject obj = npcArray.getJSONObject(i);
                String name = obj.keys().next(); // get the first (and only) key
                String dialogue = obj.getString(name);

                // Create NPC object
                NPC npc = new NPC(
                        name,
                        dialogue,
                        "default location",  // you can change if you have locations
                        0.0,                 // starting cash
                        0                    // default relationship score
                );

                npcMap.put(name, npc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return npcMap;
    }


    // -- ignore (to be changed)
    private final Map<String, NPC> npcs = new HashMap<>();
    //

    // temporary
    @Override
    public NPC getNpcByName(String name) {
        return npcs.get(name);
    }

    @Override
    public void saveNpc(NPC npc) {
        npcs.put(npc.getName(), npc);
    }
}
