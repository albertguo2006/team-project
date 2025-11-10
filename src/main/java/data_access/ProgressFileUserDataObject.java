package data_access;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import entity.Event;
import entity.NPC;
import entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProgressFileUserDataObject {

    public void JsonFileWriter(Player player){

        JsonArray saveData = new JsonArray();

        JsonObject playerData = new JsonObject();
        playerData.addProperty("name", player.getName());
        playerData.addProperty("balance", player.getBalance());
        playerData.addProperty("locationX", player.getX());
        playerData.addProperty("locationY", player.getY());

        JsonObject stats = new JsonObject();
        for(String stat : player.getStats().keySet()){
            stats.addProperty(stat, player.getStats().get(stat));
        }
        playerData.add("stats", stats);

        JsonObject npcData = new JsonObject();
        Map<NPC, Integer> npcsData = player.getRelationships();
        for (NPC npc: npcsData.keySet()) {
            npcData.addProperty(npc.getName(), npcsData.get(npc));
        }

        // TODO add event data

        saveData.add(playerData);
        saveData.add(npcData);


    }
}
