package data_access;

import entity.Event;
import entity.Item;
import entity.NPC;
import entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;
import use_case.load_progress.LoadProgressDataAccessInterface;
import use_case.save_progress.SaveProgressDataAccessInterface;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ProgressFileUserDataObject implements SaveProgressDataAccessInterface, LoadProgressDataAccessInterface {
    private static final String SAVE_FILE = "saveFile.json";

    public void save(Player player) throws IOException {
        try{
            FileWriter file = new FileWriter(SAVE_FILE, false);
            file.write(JSONFileWriter(player).toString());
            file.close();
        }
        catch(IOException e){
            // e.printStackTrace();
        }
    }

    public JSONArray JSONFileWriter(Player player){
        JSONArray saveData = new JSONArray();

        Map <NPC, Integer> npcMap = player.getRelationships();
        List<Event> eventList = player.getEvents();
        Map <Integer, Item> inventory = player.getInventory();

        JSONObject playerData = new JSONObject();
        playerData.put("name", player.getName());
        playerData.put("balance", player.getBalance());
        playerData.put("xLocation", player.getX());
        playerData.put("yLocation", player.getY());

        JSONObject stats = new JSONObject();
        for (String stat: player.getStats().keySet()) {
            stats.put(stat, player.getStats().get(stat));
        }
        playerData.put("stats", stats);

        JSONObject npcData = new JSONObject();
        for (NPC npc : npcMap.keySet()) {
            npcData.put(npc.getName(), npcMap.get(npc));
        }

        JSONObject eventData = new JSONObject();
        for (Event event : eventList) {
            eventData.put(event.getEventName(), event.getEventID());
        }

        JSONObject inventoryData= new JSONObject();
        for(int index: inventory.keySet()){
            inventoryData.put(String.valueOf(index), inventory.get(index).getName());
        }

        saveData.put(playerData);
        saveData.put(npcData);
        saveData.put(eventData);

        return saveData;

    }

    @Override
    public void load(Player player) throws IOException {
        // TODO: finish load function for loading JSON data into entities.
    }

    public void JSONFileReader(){
        // TODO: finish JsonFIle Reader function
    }

}
