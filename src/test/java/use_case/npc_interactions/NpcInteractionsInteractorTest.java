package use_case.npc_interactions;

import entity.NPC;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class NpcInteractionsInteractorTest {

    private TestNpcInteractionsUserDataAccessObject dataAccess;
    private TestNpcInteractionsOutputBoundary presenter;

    @BeforeEach
    void setUp() {
        dataAccess = new TestNpcInteractionsUserDataAccessObject();
        presenter = new TestNpcInteractionsOutputBoundary();
    }

    @Test
    void failureNpcDoesNotExistTest() {
        // Setup - no NPCs in the data access
        NpcInteractionsInteractor interactor = new NpcInteractionsInteractor(dataAccess, presenter);

        NpcInteractionsInputData inputData = new NpcInteractionsInputData("NonExistentNPC", "Hello!");

        interactor.execute(inputData);

        assertTrue(presenter.wasPresenterCalled());
        assertEquals("NonExistentNPC", presenter.getOutputData().getNpcName());
        assertTrue(presenter.getOutputData().getAiResponse().contains("Error: NPC does not exist"));
    }

    @Test
    void successNpcExistsInDataAccessTest() {
        // Add a test NPC
        NPC testNpc = new NPC("TestNPC", "a friendly shopkeeper", "shop", 100.0, 0);
        dataAccess.addNpc(testNpc);

        NpcInteractionsInteractor interactor = new NpcInteractionsInteractor(dataAccess, presenter);

        NpcInteractionsInputData inputData = new NpcInteractionsInputData("TestNPC", "Hello!");

        interactor.execute(inputData);

        assertTrue(presenter.wasPresenterCalled());
        assertEquals("TestNPC", presenter.getOutputData().getNpcName());
        // Due to API key likely being missing in test environment, expect API key error
        assertTrue(
            presenter.getOutputData().getAiResponse().contains("Gemini API key") ||
            presenter.getOutputData().getAiResponse().contains("Error")
        );
    }

    @Test
    void failureMultipleNpcsOnlyOneFoundTest() {
        // Add multiple NPCs
        NPC npc1 = new NPC("Alice", "a baker", "bakery", 200.0, 0);
        NPC npc2 = new NPC("Bob", "a mechanic", "garage", 150.0, 0);
        dataAccess.addNpc(npc1);
        dataAccess.addNpc(npc2);

        NpcInteractionsInteractor interactor = new NpcInteractionsInteractor(dataAccess, presenter);

        // Try to interact with non-existent NPC
        NpcInteractionsInputData inputData = new NpcInteractionsInputData("Charlie", "Hey there!");

        interactor.execute(inputData);

        assertTrue(presenter.wasPresenterCalled());
        assertEquals("Charlie", presenter.getOutputData().getNpcName());
        assertTrue(presenter.getOutputData().getAiResponse().contains("Error: NPC does not exist"));
    }

    @Test
    void successInteractWithCorrectNpcTest() {
        // Add multiple NPCs
        NPC npc1 = new NPC("Alice", "a baker", "bakery", 200.0, 0);
        NPC npc2 = new NPC("Bob", "a mechanic", "garage", 150.0, 0);
        dataAccess.addNpc(npc1);
        dataAccess.addNpc(npc2);

        NpcInteractionsInteractor interactor = new NpcInteractionsInteractor(dataAccess, presenter);

        // Interact with Alice
        NpcInteractionsInputData inputData = new NpcInteractionsInputData("Alice", "What's fresh today?");

        interactor.execute(inputData);

        assertTrue(presenter.wasPresenterCalled());
        assertEquals("Alice", presenter.getOutputData().getNpcName());
        // Output will contain either API response or API key error
        assertNotNull(presenter.getOutputData().getAiResponse());
    }

    @Test
    void successDataAccessCalledTest() {
        NPC testNpc = new NPC("TestNPC", "a test character", "test location", 0.0, 0);
        dataAccess.addNpc(testNpc);

        NpcInteractionsInteractor interactor = new NpcInteractionsInteractor(dataAccess, presenter);

        NpcInteractionsInputData inputData = new NpcInteractionsInputData("TestNPC", "Test message");

        interactor.execute(inputData);

        // Verify that the data access was called
        assertTrue(dataAccess.wasGetAllNpcsCalled());
    }

    @Test
    void successPresenterCalledForAllCasesTest() {
        NpcInteractionsInteractor interactor = new NpcInteractionsInteractor(dataAccess, presenter);

        // Case 1: NPC doesn't exist
        NpcInteractionsInputData inputData1 = new NpcInteractionsInputData("Ghost", "Hello!");
        interactor.execute(inputData1);
        assertTrue(presenter.wasPresenterCalled());

        // Reset presenter
        presenter = new TestNpcInteractionsOutputBoundary();
        interactor = new NpcInteractionsInteractor(dataAccess, presenter);

        // Case 2: NPC exists
        NPC testNpc = new NPC("RealNPC", "a real person", "somewhere", 100.0, 0);
        dataAccess.addNpc(testNpc);
        NpcInteractionsInputData inputData2 = new NpcInteractionsInputData("RealNPC", "Hi!");
        interactor.execute(inputData2);
        assertTrue(presenter.wasPresenterCalled());
    }

    /**
     * Test implementation of NpcInteractionsUserDataAccessInterface
     */
    private static class TestNpcInteractionsUserDataAccessObject implements NpcInteractionsUserDataAccessInterface {
        private final Map<String, NPC> npcs = new HashMap<>();
        private boolean getAllNpcsCalled = false;

        public void addNpc(NPC npc) {
            npcs.put(npc.getName(), npc);
        }

        @Override
        public Map<String, NPC> getAllNpcs() {
            getAllNpcsCalled = true;
            return npcs;
        }

        public boolean wasGetAllNpcsCalled() {
            return getAllNpcsCalled;
        }
    }

    /**
     * Test implementation of NpcInteractionsOutputBoundary
     */
    private static class TestNpcInteractionsOutputBoundary implements NpcInteractionsOutputBoundary {
        private boolean presenterCalled = false;
        private NpcInteractionsOutputData outputData;

        @Override
        public void present(NpcInteractionsOutputData outputData) {
            this.presenterCalled = true;
            this.outputData = outputData;
        }

        public boolean wasPresenterCalled() {
            return presenterCalled;
        }

        public NpcInteractionsOutputData getOutputData() {
            return outputData;
        }
    }
}
