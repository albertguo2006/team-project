package data_access;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import entity.Event;
import entity.NPC;
import entity.Player;
import use_case.load_progress.LoadProgressDataAccessInterface;
import use_case.save_progress.SaveProgressDataAccessInterface;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ProgressFileUserDataObject implements SaveProgressDataAccessInterface, LoadProgressDataAccessInterface {
    private static final String SAVE_FILE = "saveFile.json";

    public void save(Player player) throws IOException {
        try{
            FileWriter file = new FileWriter(SAVE_FILE, false);
            file.write(JsonFileWriter(player).toString());
            file.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public JsonArray JsonFileWriter(Player player){
        JsonArray saveData = new JsonArray();

        Map <NPC, Integer> npcMap = player.getRelationships();
        List<Event> eventList = player.getEvents();

        JsonObject playerData = new JsonObject();
        playerData.addProperty("name", player.getName());
        playerData.addProperty("balance", player.getBalance());
        playerData.addProperty("xLocation", player.getX());
        playerData.addProperty("yLocation", player.getY());

        JsonObject stats = new JsonObject();
        for (String stat: player.getStats().keySet()) {
            stats.addProperty(stat, player.getStats().get(stat));
        }
        playerData.add("stats", stats);

        JsonObject npcData = new JsonObject();
        for (NPC npc : npcMap.keySet()) {
            npcData.addProperty(npc.getName(), npcMap.get(npc));
        }

        JsonObject eventData = new JsonObject();
        for (Event event : eventList) {
            eventData.addProperty(event.getEventName(), event.getEventID());
        }

        saveData.add(playerData);
        saveData.add(npcData);
        saveData.add(eventData);

        return saveData;
    }

    @Override
    public void load(Player player) throws IOException {
        // TODO: finish load function for loading JSON data into entities.
    }

    public void JsonFileReader(){
        // TODO: finish JsonFIle Reader function
    }

}
