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

public class SaveFileUserDataObject implements SaveProgressDataAccessInterface {
    public static final String SAVE_FILE = "saveFile.json";
    EventDataAccessObject eventDataAccessObject = new EventDataAccessObject();

    @Override
    public void save(Player player, String currentZone) {
        try{
            FileWriter file = new FileWriter(SAVE_FILE, false);
            file.write(JSONFileWriter(player, eventDataAccessObject.createEventList(), currentZone).toString());
            file.close();
        }
        catch(IOException e){
            throw new RuntimeException();
        }
    }

    public JSONArray JSONFileWriter(Player player, List<Event> events, String currentZone) {
        JSONArray saveData = new JSONArray();

        Map <NPC, Integer> npcMap = player.getRelationships();
        List<Event> eventList = player.getEvents();
        Map <Integer, Item> inventory = player.getInventory();
        Portfolio portfolio = player.getPortfolio();
        Map<Stock, Double> investments = portfolio.getInvestments();

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
            eventData.put(event.getEventName(), events.indexOf(event));
        }

        JSONObject inventoryData = new JSONObject();
        for(int index: inventory.keySet()){
            inventoryData.put(String.valueOf(index), inventory.get(index).getName());
        }

        JSONObject portfolioData = new JSONObject();
        portfolioData.put("totalEquity", portfolio.getTotalEquity());
        JSONObject investmentData = new JSONObject();
        for (Stock stock: investments.keySet()) {
            JSONObject stockData = new JSONObject();
            stockData.put("ticketSymbol", stock.getTicketSymbol());
            stockData.put("companyName", stock.getCompanyName());
            stockData.put("stockPrice", stock.getStockPrice());

            investmentData.put(String.valueOf(investments.get(stock)), stockData);
        }
        portfolioData.put("investments", investmentData);

        saveData.put(playerData);
        saveData.put(npcData);
        saveData.put(eventData);
        saveData.put(inventoryData);
        saveData.put(portfolioData);
        saveData.put(currentZone);

        return saveData;
    }

}
