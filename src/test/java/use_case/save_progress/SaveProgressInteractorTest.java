package use_case.save_progress;

import data_access.EventDataAccessObject;
import data_access.ItemDataAccessObject;
import data_access.NPCDataAccessObject;
import data_access.SaveFileUserDataAccessObject;
import entity.*;
import interface_adapter.save_progress.SaveProgressPresenter;
import interface_adapter.save_progress.SaveProgressViewModel;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import use_case.inventory.ItemDataAccessInterface;
import use_case.npc_interactions.NpcInteractionsUserDataAccessInterface;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static data_access.LoadFileUserDataAccessObject.JSONFileReader;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SaveProgressInteractorTest {
    SaveProgressDataAccessInterface saveFileUserDAO = new SaveFileUserDataAccessObject();
    SaveProgressViewModel saveProgressViewModel = new SaveProgressViewModel();
    SaveProgressOutputBoundary saveProgressPresenter = new SaveProgressPresenter(saveProgressViewModel);
    SaveProgressInteractor saveProgressInteractor = new SaveProgressInteractor(saveFileUserDAO, saveProgressPresenter);
    NpcInteractionsUserDataAccessInterface npcDAO = new NPCDataAccessObject();
    EventDataAccessObject eventDAO = new EventDataAccessObject();
    ItemDataAccessInterface itemDAO = new ItemDataAccessObject();

    List<Event> events = eventDAO.createEventList();
    HashMap<String, Item> items = itemDAO.getItemMap();
    Map<String, NPC> npcs = npcDAO.getAllNpcs();


    // Test for successful saving
    @Test
    void successSaveProgress() throws IOException {
        GameMap gameMap = new GameMap();
        HashMap<String, Integer> stats = new HashMap<>();
        stats.put("Hunger", 30);
        stats.put("Energy", 70);
        stats.put("Mood", 80);
        Player testPlayer = new Player("Anya", 308.06, 37.2, 56.1, stats);
        testPlayer.addNPCScore(npcs.get("Bob"), 5);

        testPlayer.addEvent(events.get(0));
        testPlayer.addEvent(events.get(1));

        testPlayer.addInventory(1, items.get("Coffee"));
        testPlayer.addInventory(3, items.get("Energy Drink"));

        HashMap<Stock, Double> investments = new HashMap<>();
        Stock stock1 = new Stock("MCD", "McDonalds", 426.14);
        Stock stock2 = new Stock("NVDA", "NVIDIA Corp", 253.72);
        investments.put(stock1, 3.57);
        investments.put(stock2, 6.32);

        testPlayer.setPortfolio(new Portfolio(803.21, investments));
        testPlayer.setCurrentDay(Day.WEDNESDAY);

        String SAVE_FILE_PATH = "src/main/resources/test_JSON_files/testSuccessSaveFile.json";

        SaveProgressInputData inputData = new SaveProgressInputData(testPlayer,
                SAVE_FILE_PATH, gameMap.getCurrentZone().getName());
        saveProgressInteractor.saveGame(inputData);

        assert(testJSONFile(SAVE_FILE_PATH, testPlayer, gameMap, LocalDate.now().toString()));
        assert(testPlayer.equals(inputData.getPlayer()));
    }

    // Test for successful save, but comparing it to different Player/File Mismatch
    @Test
    void successSaveProgress_FileMismatch() throws IOException {
        Player player = new Player("Armand");
        GameMap gameMap = new GameMap();

        String SAVE_FILE_PATH = "src/main/resources/test_JSON_files/testSuccessSaveFile.json";
        SaveProgressInputData inputData = new SaveProgressInputData(player,
                SAVE_FILE_PATH, gameMap.getCurrentZone().getName());

        saveProgressInteractor.saveGame(inputData);

        HashMap<String, Integer> stats = new HashMap<>();
        stats.put("Hunger", 30);
        stats.put("Energy", 70);
        stats.put("Mood", 80);
        Player testPlayer = new Player("Anya", 308.06, 37.2, 56.1, stats);
        testPlayer.addNPCScore(npcs.get("Bob"), 5);

        assert(!testJSONFile(SAVE_FILE_PATH, testPlayer, gameMap, LocalDate.now().toString()));
    }

    // Test for file not found.
    @Test
    void failSaveProgress_ThrowsException() {
        Player player = new Player("Cynthia");
        GameMap gameMap = new GameMap();

        SaveProgressInputData inputData = new SaveProgressInputData(player,
                "invalid/file/path/.json", gameMap.getCurrentZone().getName());

        Exception exception = assertThrows(FileNotFoundException.class, () -> {
            saveProgressInteractor.saveGame(inputData);
        });
        assert(exception.getMessage().equals("Invalid Filepath! Unable to save!"));
    }

    public boolean testJSONFile(String saveFilePath, Player player, GameMap gameMap, String date) throws IOException {
        JSONArray data = JSONFileReader(saveFilePath);
        JSONObject playerData = data.getJSONObject(0);
        JSONObject relationshipData = data.getJSONObject(1);
        JSONObject eventData = data.getJSONObject(2);
        JSONObject inventoryData = data.getJSONObject(3);
        JSONObject portfolioData = data.getJSONObject(4);

        return data.length() == 7 &&
                playerDataAssertion(playerData, player) &&
                relationshipsAssertion(relationshipData, player) &&
                eventAssertion(eventData, player) &&
                inventoryAssertion(inventoryData, player) &&
                portfolioAssertion(portfolioData, player) &&
                gameMap.getCurrentZone().getName().equals(data.get(5)) &&
                date.equals(data.get(6));
    }

    public boolean inventoryAssertion(JSONObject inventoryData, Player player) {
        Map<Integer, Item> inventory = player.getInventory();

        if (inventoryData.length() != inventory.size()) {
            return false;
        }

        for (String inventoryIndex : inventoryData.keySet()) {
            Item item = items.get(inventoryData.getString(inventoryIndex));
            if (inventory.get(Integer.parseInt(inventoryIndex)) != item) {
                return false;
            }
        }
        return true;
    }

    public boolean portfolioAssertion(JSONObject portfolioData, Player player) {
        Portfolio portfolio = player.getPortfolio();
        Map<Stock, Double> investments = portfolio.getInvestments();
        JSONObject investmentData = portfolioData.getJSONObject("investments");
        if (investmentData.length() != investments.size()) {
            return false;
        }

        Map<Stock, Double> testInvestments = new HashMap<>();

        for (String shares : investmentData.keySet()) {
            JSONObject investment = investmentData.getJSONObject(shares);
            Stock testStock = new Stock(investment.getString("ticketSymbol"),
                    investment.getString("companyName"), investment.getDouble("stockPrice"));
            testInvestments.put(testStock, Double.parseDouble(shares));
        }

        return portfolioData.getDouble("totalEquity") == portfolio.getTotalEquity() &&
                testInvestments.equals(portfolio.getInvestments());
    }

    public boolean eventAssertion(JSONObject eventData, Player player) {
        int i = 0;
        List<Event> playerEvents = player.getEvents();
        if (playerEvents.size() != eventData.length()) {
            return false;
        }
        for (String eventName : eventData.keySet()) {
            if (!playerEvents.contains(events.get(eventData.getInt(eventName)))){
                return false;
            }
        }
        return true;
    }

    public boolean relationshipsAssertion(JSONObject relationshipData, Player player){
        if (relationshipData.length() != player.getRelationships().size()) {
            return false;
        }

        for (String npc : relationshipData.keySet()) {
            if (relationshipData.getInt(npc) != player.getNPCScore(npcs.get(npc))) {
                return false;
            }
        }
        return true;
    }

    public boolean playerDataAssertion(JSONObject playerData, Player player){
        JSONObject stats = playerData.getJSONObject("stats");
        Map<String, Integer> statsMap = new HashMap<>();
        for (String stat : stats.keySet()) {
            statsMap.put(stat, stats.getInt(stat));
        }

        return playerData.get("name").equals(player.getName()) &
                playerData.getDouble("balance") == player.getBalance() &
                playerData.getDouble("xLocation") == player.getX() &
                playerData.getDouble("yLocation") == player.getY() &
                statsMap.equals(player.getStats()) &
                playerData.getString("currentDay").equals(player.getCurrentDay().toString());
    }

}
