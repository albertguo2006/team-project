package use_case.sleep;

import data_access.InMemorySleepDataAccessObject;
import entity.Day;
import entity.DaySummary;
import entity.GameEnding;
import entity.Player;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SleepInteractorTest {

    private Player player;
    private InMemorySleepDataAccessObject inMemorySleepDataAccessObject;
    private SleepInteractor sleepInteractor;
    private SleepOutputBoundary testPresenter;

    @BeforeEach
    public void setUp(){
        player = new Player("TestPlayer");
        inMemorySleepDataAccessObject = new InMemorySleepDataAccessObject();
    }

    @Test
    void successSleepNormalDayTest(){
        player.setCurrentDay(Day.MONDAY);
        player.setBalance(1000.0);
        player.setHealth(50);
        player.addDailyEarnings(200.0);
        player.addDailySpending(50.0);
        player.setHasSleptToday(false);

        // Create success presenter
        testPresenter = new SleepOutputBoundary(){

            @Override
            public void presentDaySummary(SleepOutputData outputData) {
                assertNotNull(outputData);
                assertFalse(outputData.isWeekComplete());
                assertEquals(Day.TUESDAY, outputData.getNewDay());

                DaySummary summary = outputData.getSummary();
                assertEquals(Day.MONDAY, summary.getCompletedDay());
                assertEquals(200.0, summary.getEarnings(), 0.01);
                assertEquals(50.0, summary.getSpending(), 0.01);
                assertEquals(150.0, summary.getNetChange(), 0.01);
                assertEquals(1150.0, summary.getNewBalance(), 0.01);
            }

            @Override
            public void presentGameEnding(GameEnding ending) {
                fail("Should not present game ending for normal day");
            }

            @Override
            public void presentSleepError(String errorMessage) {
                fail("Should not present error: " + errorMessage);
            }
        };

        sleepInteractor = new SleepInteractor(testPresenter, inMemorySleepDataAccessObject);
        SleepInputData inputData = new SleepInputData(player);
        sleepInteractor.execute(inputData);

        // Verify player state changes
        assertEquals(100, player.getHealth()); // Health restored
        assertFalse(player.hasSleptToday()); // Not slept for the new day
        assertEquals(Day.TUESDAY, player.getCurrentDay()); // Day advanced
        assertEquals(0.0, player.getDailyEarnings(), 0.01); // Financial reset
        assertEquals(0.0, player.getDailySpending(), 0.01);
        assertEquals(1150.0, player.getBalance(), 0.01);
    }

    @Test
    void successSleepFridayWeekCompleteTest(){
        // Setup - Friday with enough money for COMFORTABLE ending
        player.setCurrentDay(Day.FRIDAY);
        player.setBalance(3000.0);
        player.setHealth(30);
        player.addDailyEarnings(500.0);
        player.addDailySpending(100.0);
        player.setHasSleptToday(false);

        // Create success presenter
        testPresenter = new SleepOutputBoundary(){

            @Override
            public void presentDaySummary(SleepOutputData outputData) {
                fail("Should not present game ending for normal day");
            }

            @Override
            public void presentGameEnding(GameEnding ending) {
                assertNotNull(ending);
                assertEquals(GameEnding.EndingType.COMFORTABLE, ending.getType());
                assertEquals(3400.0, ending.getFinalBalance(), 0.01);
                assertTrue(ending.getMessage().contains("You made it through the week with money to spare. " +
                        "You're on the right track to financial stability."));
            }

            @Override
            public void presentSleepError(String errorMessage) {
                fail("Should not present error: " + errorMessage);
            }
        };

        sleepInteractor = new SleepInteractor(testPresenter, inMemorySleepDataAccessObject);
        SleepInputData inputData = new SleepInputData(player);
        sleepInteractor.execute(inputData);

        // Verify player state changes (but day doesn't advance beyond Friday)
        assertEquals(100, player.getHealth()); // Health restored
        assertTrue(player.hasSleptToday()); // Marked as slept
        assertEquals(Day.FRIDAY, player.getCurrentDay()); // Still Friday (week complete)
        assertEquals(3400.0, player.getBalance(),  0.01);
    }

    @Test
    void failureAlreadySleptTodayTest(){
        // Setup - Player already slept today
        player.setCurrentDay(Day.WEDNESDAY);
        player.setHasSleptToday(true);

        // Create failure presenter
        testPresenter = new SleepOutputBoundary(){

            @Override
            public void presentDaySummary(SleepOutputData outputData) {
                fail("Should not present day summary when already slept");
            }

            @Override
            public void presentGameEnding(GameEnding ending) {
                fail("Should not present game ending when already slept");
            }

            @Override
            public void presentSleepError(String errorMessage) {
                assertNotNull(errorMessage);
                assertTrue(errorMessage.contains("already slept today"));
                assertTrue(errorMessage.contains("tomorrow"));
            }
        };

        sleepInteractor = new SleepInteractor(testPresenter, inMemorySleepDataAccessObject);
        SleepInputData inputData = new SleepInputData(player);
        sleepInteractor.execute(inputData);

        // Verify player state unchanged
        assertTrue(player.hasSleptToday()); // Still marked as slept
        assertEquals(Day.WEDNESDAY, player.getCurrentDay()); // Day not advanced
    }

    @Test
    void successDifferentEndingTiersTest(){
        // Test all ending tiers
        testEndingTier(6000.0, GameEnding.EndingType.WEALTHY, "Wealthy");
        testEndingTier(2500.0, GameEnding.EndingType.COMFORTABLE, "Comfortable");
        testEndingTier(1500.0, GameEnding.EndingType.STRUGGLING, "Struggling");
        testEndingTier(500.0, GameEnding.EndingType.BROKE, "Defeated");
    }

    private void testEndingTier(double balance, GameEnding.EndingType type, String description){
        player = new Player("TestPlayer");
        player.setCurrentDay(Day.FRIDAY);
        player.setBalance(balance);
        player.setHasSleptToday(false);

        testPresenter = new SleepOutputBoundary(){
            @Override
            public void presentDaySummary(SleepOutputData outputData) {
                fail("Should present game ending for Friday sleep");
            }

            @Override
            public void presentGameEnding(GameEnding ending) {
                assertEquals(type, ending.getType());
                assertEquals(balance, ending.getFinalBalance(), 0.01);
                assertNotNull(ending.getMessage());
                assertFalse(ending.getMessage().isEmpty());
            }

            @Override
            public void presentSleepError(String errorMessage) {
                fail("Should not present error for valid Friday sleep");
            }
        };

        sleepInteractor =  new SleepInteractor(testPresenter, inMemorySleepDataAccessObject);
        SleepInputData inputData = new SleepInputData(player);
        sleepInteractor.execute(inputData);
    }

    @Test
    void successFinancialTrackingResetTest(){
        // Test that daily financials reset after sleep
        player.setCurrentDay(Day.TUESDAY);
        player.setBalance(2000.0);
        player.addDailyEarnings(300.0);
        player.addDailySpending(150.0);
        player.setHasSleptToday(false);

        // Create success presenter
        testPresenter = new SleepOutputBoundary(){

            @Override
            public void presentDaySummary(SleepOutputData outputData) {
                // Verify summary shows correct financials
                DaySummary summary = outputData.getSummary();
                assertEquals(300.0, summary.getEarnings(), 0.01);
                assertEquals(150.0, summary.getSpending(), 0.01);
                assertEquals(150.0, summary.getNetChange(), 0.01);
                assertEquals(2150.0, summary.getNewBalance(), 0.01);
            }

            @Override
            public void presentGameEnding(GameEnding ending) {
                fail("Should not present game ending for Thursday");
            }

            @Override
            public void presentSleepError(String errorMessage) {
                fail("Should not present error for valid sleep");
            }
        };

        sleepInteractor = new SleepInteractor(testPresenter, inMemorySleepDataAccessObject);
        SleepInputData inputData = new SleepInputData(player);
        sleepInteractor.execute(inputData);

        // Verify financials reset for new day
        assertEquals(0.0, player.getDailyEarnings(), 0.01);
        assertEquals(0.0, player.getDailySpending(), 0.01);
        assertEquals(2150.0, player.getBalance(), 0.01);
    }

    @Test
    void successHealthRestorationTest() {
        // Test various health restoration scenarios
        testHealthRestoration(25); // Low health -> full restoration
        testHealthRestoration(75); // Medium health -> full restoration
        testHealthRestoration(100); // Already full health -> stays full

    }

    private void testHealthRestoration(int initialHealth){
        player = new Player("TestPlayer");
        player.setCurrentDay(Day.TUESDAY);
        player.setHealth(initialHealth);
        player.setHasSleptToday(false);

        testPresenter = new SleepOutputBoundary(){

            @Override
            public void presentDaySummary(SleepOutputData outputData) {
                // Success case - verify in presenter callback
            }

            @Override
            public void presentGameEnding(GameEnding ending) {
                fail("Should not present game ending for Tuesday");
            }

            @Override
            public void presentSleepError(String errorMessage) {
                fail("Should not present error for valid sleep");
            }
        };

        sleepInteractor = new SleepInteractor(testPresenter, inMemorySleepDataAccessObject);
        SleepInputData inputData = new SleepInputData(player);
        sleepInteractor.execute(inputData);

        // Verify health restoration
        assertEquals(100, player.getHealth(), 0.01);
    }

    @Test
    void failureNullPlayerTest(){
        // Test handling of null player
        testPresenter = new  SleepOutputBoundary(){

            @Override
            public void presentDaySummary(SleepOutputData outputData) {
                fail("Should not present day summary with null player");
            }

            @Override
            public void presentGameEnding(GameEnding ending) {
                fail("Should not present game ending with null player");
            }

            @Override
            public void presentSleepError(String errorMessage) {
                assertNotNull(errorMessage);
                assertTrue(errorMessage.contains("Player not found"));
            }
        };

        sleepInteractor = new SleepInteractor(testPresenter, inMemorySleepDataAccessObject);
        SleepInputData inputData = new SleepInputData(null);

        // Should not throw exception, should handle gracefully
        assertDoesNotThrow(() -> sleepInteractor.execute(inputData));
    }

    @Test
    void failureDayAdvancementTest() {
        // Setup - Create a scenario where advanceDay() returns false unexpectedly
        Player player = new Player("TestPlayer");
        player.setCurrentDay(Day.WEDNESDAY); // Not Friday, so should advance
        player.setHasSleptToday(false);
        player.setHealth(50);

        // Create a mock player that always fails to advance
        Player failingPlayer = new Player("FailingPlayer") {
            @Override
            public boolean advanceDay() {
                return false; // Simulate advancement failure
            }
        };
        failingPlayer.setCurrentDay(Day.WEDNESDAY);
        failingPlayer.setHasSleptToday(false);
        failingPlayer.setHealth(50);

        // Create failure presenter
        testPresenter = new SleepOutputBoundary() {
            @Override
            public void presentDaySummary(SleepOutputData outputData) {
                fail("Should not present day summary when day advancement fails");
            }

            @Override
            public void presentGameEnding(GameEnding ending) {
                fail("Should not present game ending for Wednesday sleep");
            }

            @Override
            public void presentSleepError(String errorMessage) {
                // This is the expected path
                assertNotNull(errorMessage);
                assertTrue(errorMessage.contains("Unable to advance to next day"));
            }
        };

        sleepInteractor = new SleepInteractor(testPresenter, inMemorySleepDataAccessObject);
        SleepInputData inputData = new SleepInputData(failingPlayer);
        sleepInteractor.execute(inputData);

        // Verify player state - should still have slept but day not advanced
        assertTrue(failingPlayer.hasSleptToday()); // Sleep was processed
        assertEquals(Day.WEDNESDAY, failingPlayer.getCurrentDay()); // Day didn't advance
        assertEquals(100, failingPlayer.getHealth()); // Health still restored
    }
}






