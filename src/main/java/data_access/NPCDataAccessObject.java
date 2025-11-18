package data_access;

import entity.NPC;
import org.json.JSONArray;
import org.json.JSONObject;
import use_case.npc_interactions.NpcInteractionsUserDataAccessInterface;

import java.io.IOException;
import java.util.HashMap;

import static data_access.LoadFileUserDataAccessObject.JSONFileReader;

public class NPCDataAccessObject implements NpcInteractionsUserDataAccessInterface {
    private static final String NPC_FILE = "npc.json";

    @Override
    public HashMap<String, NPC> getNPCMap() {
        try{
            JSONArray data = JSONFileReader(NPC_FILE);
            HashMap<String, NPC> npcMap = new HashMap<>();
            for (int i = 0; i < data.length(); i++) {
                JSONObject npcData = data.getJSONObject(i);
                NPC npc = new NPC(npcData.getString("name"), npcData.getString("dialoguePrompt"),
                        npcData.getString("location"), npcData.getDouble("cashBalance"),
                        npcData.getInt("defaultScore"));
                npcMap.put(npcData.getString("name"), npc);
            }
            return npcMap;
        }
        catch (IOException e){
            throw new RuntimeException();
        }
    }
}




