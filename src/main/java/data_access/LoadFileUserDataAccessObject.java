package data_access;

import entity.*;
import org.json.JSONArray;
import org.json.JSONObject;
import use_case.load_progress.LoadProgressDataAccessInterface;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static data_access.SaveFileUserDataObject.SAVE_FILE;

public class LoadFileUserDataAccessObject implements LoadProgressDataAccessInterface {
    EventDataAccessObject eventDataAccessObject = new EventDataAccessObject();
    ItemDataAccessObject itemDataAccessObject = new ItemDataAccessObject();
    NPCDataAccessObject npcDataAccessObject = new NPCDataAccessObject();

    public static JSONArray JSONFileReader(String FILE_NAME) throws IOException {
        try{
            String data = Files.readString(Paths.get(FILE_NAME));
            return new JSONArray(data);
        }
        catch(IOException e){
            throw new RuntimeException();
        }
    }

    @Override
    public Player load(GameMap gameMap) throws IOException {
        JSONArray data = JSONFileReader(SAVE_FILE);
        List<Event> events = eventDataAccessObject.createEventList();
        HashMap<String, Item> items = itemDataAccessObject.getItemMap();
        HashMap<String, NPC> npcMap = npcDataAccessObject.getNPCMap();

        Player player = loadPlayer(data);
        loadInventory(data, player, items);
        loadEvents(data, player, events);
        loadRelationships(data, player, npcMap);
        loadPortfolio(data, player);

        gameMap.setCurrentZone(data.getString(5));

        return player;
    }

    public Player loadPlayer(JSONArray data) {
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

    public void loadEvents(JSONArray data, Player player, List<Event> events) {
        JSONObject eventData = data.getJSONObject(2);

        for (String key: eventData.keySet()){
            player.addEvent(events.get(eventData.getInt(key)));
        }

    }

    public void loadRelationships(JSONArray data, Player player, HashMap<String, NPC> npcMap) {
        JSONObject relationshipData = data.getJSONObject(1);
        for(String npcName: relationshipData.keySet()){
            NPC npc = npcMap.get(npcName);
            player.addNPCScore(npc, relationshipData.getInt(npcName));
        }

    }

    public void loadInventory(JSONArray data, Player player, HashMap<String, Item> items) {

        JSONObject inventoryData = data.getJSONObject(3);
        for (String item: inventoryData.keySet()) {
            player.addInventory(Integer.parseInt(item), items.get(inventoryData.getString(item)));
        }
    }

    public void loadPortfolio(JSONArray data, Player player) {
        JSONObject portfolioData = data.getJSONObject(4);
        Double equity = portfolioData.getDouble("totalEquity");
        JSONObject investmentData = portfolioData.getJSONObject("investments");
        HashMap<Stock, Double> investments= new HashMap<>();
        for (String shares: investmentData.keySet()) {
            JSONObject stockData = investmentData.getJSONObject(shares);
            Stock stock = new Stock(stockData.getString("ticketSymbol"),
                    stockData.getString("companyName"),
                    stockData.getDouble("stockPrice"));
            investments.put(stock, investmentData.getDouble(shares));
        }
        Portfolio portfolio = new Portfolio(equity, investments);
        player.setPortfolio(portfolio);
    }
}





