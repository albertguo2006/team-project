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

        testPlayer.addEvent(events.get(1));  // Fixed: JSON has "Test Event A": 1
        testPlayer.addEvent(events.get(0));  // Fixed: JSON has "Car Accident": 0

        testPlayer.addInventory(1, items.get("Coffee"));
        testPlayer.addInventory(3, items.get("Energy Drink"));

        HashMap<Stock, Double> investments = new HashMap<>();
        Stock stock1 = new Stock("MCD", "McDonalds", 426.14);
        Stock stock2 = new Stock("NVDA", "NVIDIA Corp", 253.72);
        investments.put(stock1, 3.57);
        investments.put(stock2, 6.32);

        testPlayer.setPortfolio(new Portfolio(803.21, investments));

        assert(gameMap.getCurrentZone().getName().equals("Subway Station 1"));

        // Debug: Check each field individually
        System.out.println("Name match: " + player.getName().equals(testPlayer.getName()));
        System.out.println("Balance match: " + (player.getBalance() == testPlayer.getBalance()));
        System.out.println("X match: " + (player.getX() == testPlayer.getX()));
        System.out.println("Y match: " + (player.getY() == testPlayer.getY()));
        System.out.println("Stats match: " + player.getStats().equals(testPlayer.getStats()));
        System.out.println("Events match: " + player.getEvents().equals(testPlayer.getEvents()));
        System.out.println("Loaded events size: " + player.getEvents().size());
        System.out.println("Test events size: " + testPlayer.getEvents().size());
        for (int i = 0; i < player.getEvents().size(); i++) {
            System.out.println("  Loaded event " + i + ": " + player.getEvents().get(i).getEventName());
        }
        for (int i = 0; i < testPlayer.getEvents().size(); i++) {
            System.out.println("  Test event " + i + ": " + testPlayer.getEvents().get(i).getEventName());
        }
        System.out.println("Inventory match: " + player.getInventory().equals(testPlayer.getInventory()));
        System.out.println("Portfolio match: " + player.getPortfolio().equals(testPlayer.getPortfolio()));
        System.out.println("Relationships match: " + player.getRelationships().equals(testPlayer.getRelationships()));

        System.out.println("\nLoaded portfolio cash: " + player.getPortfolio().getCash());
        System.out.println("Test portfolio cash: " + testPlayer.getPortfolio().getCash());
        System.out.println("Loaded portfolio totalEquity: " + player.getPortfolio().totalEquity);
        System.out.println("Test portfolio totalEquity: " + testPlayer.getPortfolio().totalEquity);
        System.out.println("Loaded portfolio investments: " + player.getPortfolio().getInvestments());
        System.out.println("Test portfolio investments: " + testPlayer.getPortfolio().getInvestments());

        assert(player.equals(testPlayer));
    }
}
