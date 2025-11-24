package use_case.load_progress;

import data_access.EventDataAccessObject;
import data_access.ItemDataAccessObject;
import data_access.LoadFileUserDataAccessObject;
import data_access.NPCDataAccessObject;
import entity.*;
import interface_adapter.load_progress.LoadProgressPresenter;
import org.junit.jupiter.api.Test;
import use_case.inventory.ItemDataAccessInterface;
import use_case.npc_interactions.NpcInteractionsUserDataAccessInterface;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class LoadProgressInteractorTest {
    LoadProgressDataAccessInterface loadFileDAO = new LoadFileUserDataAccessObject();
    LoadProgressOutputBoundary loadProgressPresenter = new LoadProgressPresenter();
    LoadProgressInteractor loadProgressInteractor = new LoadProgressInteractor(loadFileDAO, loadProgressPresenter);
    NpcInteractionsUserDataAccessInterface npcDAO = new NPCDataAccessObject();
    EventDataAccessObject eventDAO = new EventDataAccessObject();
    ItemDataAccessInterface itemDAO = new ItemDataAccessObject();

    List<Event> events = eventDAO.createEventList();
    HashMap<String, Item> items = itemDAO.getItemMap();
    Map<String, NPC> npcs = npcDAO.getAllNpcs();

    @Test
    void successLoadProgress() {
        GameMap gameMap = new GameMap();

        LoadProgressInputData inputData = new LoadProgressInputData(gameMap, "src/main/resources/testLoadFile.json");
        Player player = loadProgressInteractor.loadGame(inputData);
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

        assert(gameMap.getCurrentZone().getName().equals("Subway Station 1"));
        assert(player.equals(testPlayer));
    }

    @Test
    void successLoadProgress_PlayerMismatch() {
        GameMap gameMap = new GameMap();

        LoadProgressInputData inputData = new LoadProgressInputData(gameMap, "src/main/resources/testLoadFile.json");
        Player player = loadProgressInteractor.loadGame(inputData);
        HashMap<String, Integer> stats = new HashMap<>();
        stats.put("Hunger", 30);
        stats.put("Energy", 70);
        stats.put("Mood", 80);
        Player testPlayer = new Player("Anya", 308.06, 37.2, 56.1, stats);
        testPlayer.addNPCScore(npcs.get("Sebestian"), 5);

        testPlayer.addEvent(events.get(1));

        assert(!player.equals(testPlayer));
    }

    @Test
    void failLoadProgress_FileNotFound() {
        GameMap gameMap = new GameMap();
        LoadProgressInputData inputData = new LoadProgressInputData(gameMap, "this/file/is/invalid.json");
        Exception exception = assertThrows(FileNotFoundException.class, () -> {
            loadProgressInteractor.loadGame(inputData);
        });
        assert(exception.getMessage().equals("Save File not found! Cannot load progress into Game!"));
        // Considering making custom exceptions.... but IDK what custom exceptions are actually for....

    }
}
