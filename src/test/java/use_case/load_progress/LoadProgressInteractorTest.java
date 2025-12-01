package use_case.load_progress;

import data_access.EventDataAccessObject;
import data_access.ItemDataAccessObject;
import data_access.LoadFileUserDataAccessObject;
import data_access.NPCDataAccessObject;
import entity.*;
import interface_adapter.load_progress.LoadProgressPresenter;
import interface_adapter.load_progress.LoadProgressViewModel;
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
    LoadProgressViewModel loadProgressViewModel = new LoadProgressViewModel();
    LoadProgressOutputBoundary loadProgressPresenter = new LoadProgressPresenter(loadProgressViewModel);
    LoadProgressInteractor loadProgressInteractor = new LoadProgressInteractor(loadFileDAO, loadProgressPresenter);
    NpcInteractionsUserDataAccessInterface npcDAO = new NPCDataAccessObject();
    EventDataAccessObject eventDAO = new EventDataAccessObject();
    ItemDataAccessInterface itemDAO = new ItemDataAccessObject();

    List<Event> events = eventDAO.createEventList();
    HashMap<String, Item> items = itemDAO.getItemMap();
    Map<String, NPC> npcs = npcDAO.getAllNpcs();

    // Test for successful loading in file.
    @Test
    void successLoadProgress() throws IOException {
        GameMap gameMap = new GameMap();

        LoadProgressInputData inputData = new LoadProgressInputData(gameMap, "src/main/resources/test_JSON_files/testLoadFile.json");
        Player player = loadProgressInteractor.loadGame(inputData);
        HashMap<String, Integer> stats = new HashMap<>();
        stats.put("Hunger", 30);
        stats.put("Energy", 70);
        stats.put("Mood", 80);
        Player testPlayer = new Player("Anya", 308.06, 1567.9, 601.2, stats);
        testPlayer.addNPCScore(npcs.get("Bob"), 5);
        testPlayer.setCurrentDay(Day.TUESDAY);

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
        List<Event> eventsTest = testPlayer.getEvents();

        assert(player.equals(testPlayer));
        assert(gameMap.getCurrentZone().getName().equals("Subway Station 1"));
        assert(eventsTest.get(0).getEventName().equals("Subway Busker"));
        assert(eventsTest.get(1).getEventName().equals("Fallen Groceries"));
        assert(loadProgressViewModel.getRecentSaveDate().equals("2025-10-30"));
    }

    // Test for successful loading in file, but the player does not match with the loaded in Player.
    @Test
    void successLoadProgress_PlayerMismatch() throws IOException {
        GameMap gameMap = new GameMap();

        LoadProgressInputData inputData = new LoadProgressInputData(gameMap, "src/main/resources/test_JSON_files/testLoadFile.json");
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

    // Test for file not found.
    @Test
    void failLoadProgress_FileNotFound() {
        GameMap gameMap = new GameMap();
        LoadProgressInputData inputData = new LoadProgressInputData(gameMap, "this/file/is/invalid.json");
        Exception exception = assertThrows(FileNotFoundException.class, () -> {
            loadProgressInteractor.loadGame(inputData);
        });
        assert(exception.getMessage().equals("Save File not found! Cannot load progress into Game!"));

    }
}
