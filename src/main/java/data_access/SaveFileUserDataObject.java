package data_access;

import entity.*;
import org.json.JSONArray;
import org.json.JSONObject;
import use_case.load_progress.LoadProgressDataAccessInterface;
import use_case.save_progress.SaveProgressDataAccessInterface;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaveFileUserDataObject implements SaveProgressDataAccessInterface, LoadProgressDataAccessInterface {
    private static final String SAVE_FILE = "saveFile.json";
    private static final String EVENT_FILE = "events.json";

    public void save(Player player) throws IOException {
        try{
            FileWriter file = new FileWriter(SAVE_FILE, false);
            file.write(JSONFileWriter(player).toString());
            file.close();
        }
        catch(IOException e){
            // TODO: handle exception
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
    public Player load() throws IOException {
        return new Player("bob");
        // TODO: properly implement this function.
    }

    public Player loadPlayer() throws IOException{
        JSONArray data = JSONFileReader(SAVE_FILE);
        JSONObject playerData = data.getJSONObject(0);
        JSONObject statsData = playerData.getJSONObject("stats");

        Map<String, Integer> stats = new HashMap<>();
        for (String stat: statsData.keySet()) {
            stats.put(stat, statsData.getInt(stat));
        }

        Player player = new Player(playerData.getString("name"),
                playerData.getDouble("balance"),
                playerData.getDouble("xLocation"),
                playerData.getDouble("yLocation"),
                stats);

        return player;
    }

    public JSONArray JSONFileReader(String FILE_NAME) throws IOException {
        try{
            String data = Files.readString(Paths.get(FILE_NAME));
            return new JSONArray(data);
        }
        catch(IOException e){
            throw new IOException(e.getMessage());
            // TODO handle exception. Idk if the above is right or not
        }
    }

}
