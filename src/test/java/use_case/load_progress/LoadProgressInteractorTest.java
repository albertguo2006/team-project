package use_case.load_progress;

import data_access.EventDataAccessObject;
import data_access.ItemDataAccessObject;
import data_access.LoadFileUserDataAccessObject;
import data_access.NPCDataAccessObject;
import entity.*;
import org.junit.jupiter.api.Test;
import use_case.inventory.ItemDataAccessInterface;
import use_case.npc_interactions.NpcInteractionsUserDataAccessInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoadProgressInteractorTest {
    LoadProgressDataAccessInterface loadFileDAO = new LoadFileUserDataAccessObject();
    LoadProgressInteractor loadProgressInteractor = new LoadProgressInteractor(loadFileDAO);
    NpcInteractionsUserDataAccessInterface npcDAO = new NPCDataAccessObject();
    EventDataAccessObject eventDAO = new EventDataAccessObject();
    ItemDataAccessInterface itemDAO = new ItemDataAccessObject();

    List<Event> events = eventDAO.createEventList();
    HashMap<String, Item> items = itemDAO.getItemMap();
    HashMap<String, NPC> npcs = (HashMap<String, NPC>) npcDAO.getAllNpcs();

    @Test
    void successLoadProgress() throws IOException {
        GameMap gameMap = new GameMap();
        Player player = loadProgressInteractor.loadGame(gameMap, "src/main/resources/testLoadFile.json");
        HashMap<String, Integer> stats = new HashMap<>();
        stats.put("Hunger", 30);
        stats.put("Energy", 70);
        stats.put("Mood", 80);
        Player testPlayer = new Player("Anya", 308.06, 37.2, 56.1, stats);
        testPlayer.addNPCScore(npcs.get("Bob"), 5);  // Fixed: JSON has 5, not 8

        testPlayer.addEvent(events.get(0));  // Car Accident (loaded first due to JSON key order)
        testPlayer.addEvent(events.get(1));  // Test Event A (loaded second)

        testPlayer.addInventory(1, items.get("Coffee"));
        testPlayer.addInventory(3, items.get("Energy Drink"));

        HashMap<Stock, Double> investments = new HashMap<>();
        Stock stock1 = new Stock("MCD", "McDonalds", 426.14);
        Stock stock2 = new Stock("NVDA", "NVIDIA Corp", 253.72);
        investments.put(stock1, 3.57);
        investments.put(stock2, 6.32);

        testPlayer.setPortfolio(new Portfolio(803.21, investments));

        assert(gameMap.getCurrentZone().getName().equals("Subway Station 1"));
        assert(player.equals(testPlayer));
    }
}
