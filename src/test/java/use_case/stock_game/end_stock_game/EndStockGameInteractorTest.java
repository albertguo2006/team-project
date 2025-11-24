package use_case.stock_game.end_stock_game;

import entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EndStockGameInteractorTest {

    private EndStockGameInteractor interactor;
    private TestEndStockGameDataAccessObject dataAccessObject;
    private TestEndStockGameOutputBoundary presenter;

    @BeforeEach
    void setUp() {
        dataAccessObject = new TestEndStockGameDataAccessObject();
        presenter = new TestEndStockGameOutputBoundary();
        interactor = new EndStockGameInteractor(dataAccessObject, presenter);
    }

    @Test
    void successEndGameWithPositiveEquityTest() {
        dataAccessObject.setTotalEquity(1500.0);

        interactor.execute();

        assertTrue(presenter.wasSuccessViewCalled());
        assertNotNull(presenter.getOutputData());
        assertEquals(1500.0, presenter.getOutputData().getTotalEquity(), 0.01);
    }

    @Test
    void successEndGameWithZeroEquityTest() {
        dataAccessObject.setTotalEquity(0.0);

        interactor.execute();

        assertTrue(presenter.wasSuccessViewCalled());
        assertNotNull(presenter.getOutputData());
        assertEquals(0.0, presenter.getOutputData().getTotalEquity(), 0.01);
    }

    @Test
    void successEndGameWithNegativeEquityTest() {
        dataAccessObject.setTotalEquity(-500.0);

        interactor.execute();

        assertTrue(presenter.wasSuccessViewCalled());
        assertNotNull(presenter.getOutputData());
        assertEquals(-500.0, presenter.getOutputData().getTotalEquity(), 0.01);
    }

    @Test
    void successEndGameWithLargeEquityTest() {
        dataAccessObject.setTotalEquity(10000.0);

        interactor.execute();

        assertTrue(presenter.wasSuccessViewCalled());
        assertNotNull(presenter.getOutputData());
        assertEquals(10000.0, presenter.getOutputData().getTotalEquity(), 0.01);
    }

    @Test
    void successEndGameMultipleTimesTest() {
        // Test ending game multiple times with different equity values
        dataAccessObject.setTotalEquity(1000.0);
        interactor.execute();
        assertEquals(1000.0, presenter.getOutputData().getTotalEquity(), 0.01);

        dataAccessObject.setTotalEquity(2000.0);
        interactor.execute();
        assertEquals(2000.0, presenter.getOutputData().getTotalEquity(), 0.01);

        dataAccessObject.setTotalEquity(500.0);
        interactor.execute();
        assertEquals(500.0, presenter.getOutputData().getTotalEquity(), 0.01);
    }

    /**
     * Test implementation of EndStockGameDataAccessInterface
     */
    private static class TestEndStockGameDataAccessObject implements EndStockGameDataAccessInterface {
        private Double totalEquity = 0.0;

        @Override
        public Double getTotalEquity() {
            return totalEquity;
        }

        @Override
        public Player get(String username) {
            // Not used in current implementation
            return null;
        }

        @Override
        public void updateBalanceEarnings(Player player, Double totalEquity) {
            // Not used in current implementation
        }

        public void setTotalEquity(Double totalEquity) {
            this.totalEquity = totalEquity;
        }
    }

    /**
     * Test implementation of EndStockGameOutputBoundary
     */
    private static class TestEndStockGameOutputBoundary implements EndStockGameOutputBoundary {
        private boolean successViewCalled = false;
        private EndStockGameOutputData outputData;

        @Override
        public void prepareSuccessView(EndStockGameOutputData outputData) {
            this.successViewCalled = true;
            this.outputData = outputData;
        }

        public boolean wasSuccessViewCalled() {
            return successViewCalled;
        }

        public EndStockGameOutputData getOutputData() {
            return outputData;
        }
    }
}
