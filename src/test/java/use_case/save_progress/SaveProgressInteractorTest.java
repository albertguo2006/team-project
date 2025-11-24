package use_case.save_progress;

import data_access.EventDataAccessObject;
import data_access.ItemDataAccessObject;
import data_access.NPCDataAccessObject;
import data_access.SaveFileUserDataObject;
import entity.*;
import interface_adapter.save_progress.SaveProgressPresenter;
import org.junit.jupiter.api.Test;
import use_case.inventory.ItemDataAccessInterface;
import use_case.npc_interactions.NpcInteractionsUserDataAccessInterface;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SaveProgressInteractorTest {
    SaveProgressDataAccessInterface saveFileUserDAO = new SaveFileUserDataObject();
    SaveProgressOutputBoundary saveProgressPresenter = new SaveProgressPresenter();
    SaveProgressInteractor saveProgressInteractor = new SaveProgressInteractor(saveFileUserDAO, saveProgressPresenter);
    NpcInteractionsUserDataAccessInterface npcDAO = new NPCDataAccessObject();
    EventDataAccessObject eventDAO = new EventDataAccessObject();
    ItemDataAccessInterface itemDAO = new ItemDataAccessObject();

    List<Event> events = eventDAO.createEventList();
    HashMap<String, Item> items = itemDAO.getItemMap();
    Map<String, NPC> npcs = npcDAO.getAllNpcs();


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

        SaveProgressInputData inputData = new SaveProgressInputData(testPlayer,
                "src/main/resources/testSuccessSaveFile.json", gameMap.getCurrentZone().getName());
        saveProgressInteractor.saveGame(inputData);

        // TODO: write assertions for this,,  a bit unsure on how to do so when comparing JSON files
    }

    @Test
    void successSaveProgress_FileMismatch() throws IOException {
        Player player = new Player("Armand");
        GameMap gameMap = new GameMap();

        SaveProgressInputData inputData = new SaveProgressInputData(player,
                "src/main/resources/testSuccessSaveFile.json", gameMap.getCurrentZone().getName());

        saveProgressInteractor.saveGame(inputData);


        // TODO: write assertions for this,,  a bit unsure on how to do so when comparing JSON files
    }

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
        // Considering making custom exceptions.... but IDK what custom exceptions are actually for....
    }

}
