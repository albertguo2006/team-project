package use_case.save_progress;

import entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class SaveProgressInteractorTest {

    private SaveProgressInteractor saveProgressInteractor;
    private TestSaveProgressDataAccessObject dataAccessObject;

    @BeforeEach
    void setUp() {
        dataAccessObject = new TestSaveProgressDataAccessObject();
        saveProgressInteractor = new SaveProgressInteractor(dataAccessObject);
    }

    @Test
    void successSaveGameTest() throws IOException {
        Player player = new Player("TestPlayer");
        player.setBalance(1500.0);
        player.setX(100.0);
        player.setY(200.0);
        String currentZone = "home";
        String saveFile = "test_save.json";

        saveProgressInteractor.saveGame(player, currentZone, saveFile);

        assertTrue(dataAccessObject.wasSaveCalled());
        assertEquals(player, dataAccessObject.getSavedPlayer());
        assertEquals(currentZone, dataAccessObject.getSavedZone());
        assertEquals(saveFile, dataAccessObject.getSavedFile());
    }

    @Test
    void saveGameWithDifferentPlayerStatesTest() throws IOException {
        Player player1 = new Player("Player1");
        player1.setBalance(5000.0);

        Player player2 = new Player("Player2");
        player2.setBalance(100.0);

        saveProgressInteractor.saveGame(player1, "office", "save1.json");
        assertEquals(player1, dataAccessObject.getSavedPlayer());
        assertEquals("office", dataAccessObject.getSavedZone());

        saveProgressInteractor.saveGame(player2, "subway", "save2.json");
        assertEquals(player2, dataAccessObject.getSavedPlayer());
        assertEquals("subway", dataAccessObject.getSavedZone());
    }

    @Test
    void saveGameThrowsIOExceptionTest() {
        Player player = new Player("TestPlayer");
        String currentZone = "home";
        String saveFile = "test_save.json";

        dataAccessObject.setShouldThrowException(true);

        assertThrows(IOException.class, () ->
            saveProgressInteractor.saveGame(player, currentZone, saveFile)
        );
    }

    /**
     * Test implementation of SaveProgressDataAccessInterface
     */
    private static class TestSaveProgressDataAccessObject implements SaveProgressDataAccessInterface {
        private boolean saveCalled = false;
        private Player savedPlayer;
        private String savedZone;
        private String savedFile;
        private boolean shouldThrowException = false;

        @Override
        public void save(Player player, String currentZone, String saveFile) throws IOException {
            if (shouldThrowException) {
                throw new IOException("Test exception");
            }
            this.saveCalled = true;
            this.savedPlayer = player;
            this.savedZone = currentZone;
            this.savedFile = saveFile;
        }

        public boolean wasSaveCalled() {
            return saveCalled;
        }

        public Player getSavedPlayer() {
            return savedPlayer;
        }

        public String getSavedZone() {
            return savedZone;
        }

        public String getSavedFile() {
            return savedFile;
        }

        public void setShouldThrowException(boolean shouldThrowException) {
            this.shouldThrowException = shouldThrowException;
        }
    }
}
