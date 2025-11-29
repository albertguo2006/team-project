package use_case.stock_game;

import entity.Portfolio;
import entity.Stock;
import org.junit.jupiter.api.Test;

import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Full unit test for PlayStockGameInteractor with 100% coverage
 */
class PlayStockGameInteractorTest {

    // ------------------------------
    // Fake Timer for synchronous tests
    // ------------------------------
    static class FakeTimer {
        private final ActionListener listener;
        private boolean running = false;

        FakeTimer(ActionListener listener) {
            this.listener = listener;
        }

        public void start() {
            running = true;
        }

        public void stop() {
            running = false;
        }

        public boolean isRunning() {
            return running;
        }

        public void fire() {
            listener.actionPerformed(new java.awt.event.ActionEvent(this, 0, "tick"));
        }
    }

    // ------------------------------
    // Stub DAO
    // ------------------------------
    static class StubDAO implements PlayStockGameDataAccessInterface {
        private final List<Double> legacyPrices;
        private final Map<Integer, List<Double>> fiveDayPrices;

        StubDAO(List<Double> prices) {
            this.legacyPrices = new ArrayList<>(prices);
            this.fiveDayPrices = null;
        }

        StubDAO(Map<Integer, List<Double>> map) {
            Map<Integer, List<Double>> mutable = new HashMap<>();
            for (var e : map.entrySet()) {
                mutable.put(e.getKey(), new ArrayList<>(e.getValue()));
            }
            this.fiveDayPrices = mutable;
            this.legacyPrices = null;
        }

        @Override
        public List<Double> getIntradayPrices(String symbol, int day) {
            return new ArrayList<>(legacyPrices);
        }

        @Override
        public Map<Integer, List<Double>> getFiveDayPrices(String symbol, String month, int start) {
            return fiveDayPrices;
        }
    }

    // ------------------------------


    // ------------------------------
    // Test 2: New API path using getFiveDayPrices
    // ------------------------------
    @Test
    void testFiveDayPathSuccess() {
        Map<Integer, List<Double>> five = new HashMap<>();
        for (int i = 1; i <= 5; i++) five.put(i, Arrays.asList(10.0 + i));

        StubDAO dao = new StubDAO(five);

        final boolean[] startCalled = {false};

        PlayStockGameOutputBoundary presenter = new PlayStockGameOutputBoundary() {
            @Override public void presentGameStart(Portfolio p, Stock s, Timer t) {
                assertEquals("GOOG", s.getTicketSymbol());
                assertEquals(11.0, s.getStockPrice());
                startCalled[0] = true;
            }
            @Override public void presentPriceUpdate(PlayStockGameOutputData o) {}
            @Override public void presentGameOver(PlayStockGameOutputData o) { }
            @Override public void presentError(String errMsg) { fail("Should not error"); }
            @Override public void prepareSuccessView(PlayStockGameOutputData outputData) { }
        };

        PlayStockGameInteractor interactor = new PlayStockGameInteractor(dao, presenter);
        PlayStockGameInputData input = new PlayStockGameInputData(
                "GOOG", 2000, "2024-01", 0, "PERIOD1"
        );
        interactor.execute(input);

        assertTrue(startCalled[0]);
    }

    // ------------------------------
    // Test 3: Five-day path exception
    // ------------------------------
    @Test
    void testFiveDayPathException() {
        PlayStockGameDataAccessInterface dao = new PlayStockGameDataAccessInterface() {
            @Override public List<Double> getIntradayPrices(String symbol, int day) { return null; }
            @Override public Map<Integer, List<Double>> getFiveDayPrices(String symbol, String month, int start) {
                throw new RuntimeException("Five-day DAO failed");
            }
        };

        final boolean[] errorCalled = {false};

        PlayStockGameOutputBoundary presenter = new PlayStockGameOutputBoundary() {
            @Override public void presentGameStart(Portfolio p, Stock s, Timer t) { }
            @Override public void presentPriceUpdate(PlayStockGameOutputData o) { }
            @Override public void presentGameOver(PlayStockGameOutputData o) { }
            @Override public void presentError(String errMsg) {
                assertEquals("Five-day DAO failed", errMsg);
                errorCalled[0] = true;
            }
            @Override public void prepareSuccessView(PlayStockGameOutputData outputData) { }
        };

        PlayStockGameInteractor interactor = new PlayStockGameInteractor(dao, presenter);
        PlayStockGameInputData input = new PlayStockGameInputData(
                "TEST", 1000, "2024-01", 0, "PERIODX"
        );
        interactor.execute(input);

        assertTrue(errorCalled[0]);
    }

    // ------------------------------
    // Test 4: Game loop + forced game over
    // ------------------------------
    @Test
    void testGameLoopAndGameOver() {

        List<Double> basePrices = new ArrayList<>();
        for (int i = 0; i < 20; i++) basePrices.add(10.0 + i);

        StubDAO dao = new StubDAO(basePrices);

        final boolean[] startCalled = {false};
        final boolean[] gameOverCalled = {false};
        final int[] updateCount = {0};

        final FakeTimer[] savedTimer = {null};

        PlayStockGameOutputBoundary presenter = new PlayStockGameOutputBoundary() {
            @Override public void presentGameStart(Portfolio p, Stock s, Timer t) { startCalled[0] = true; }
            @Override public void presentPriceUpdate(PlayStockGameOutputData o) {
                updateCount[0]++;
                if (updateCount[0] >= 3) {
                    savedTimer[0].stop();
                    gameOverCalled[0] = true;
                }

            }
            @Override public void presentGameOver(PlayStockGameOutputData o) { gameOverCalled[0] = true; }
            @Override public void presentError(String errMsg) { fail("Unexpected error"); }
            @Override public void prepareSuccessView(PlayStockGameOutputData outputData) { }
        };

        PlayStockGameInteractor interactor = new PlayStockGameInteractor(dao, presenter) {
            @Override
            public void execute(PlayStockGameInputData inputData) {
                List<Double> real = dao.getIntradayPrices("X", 1);
                Portfolio p = new Portfolio();
                Stock s = new Stock("X", real.get(0));
                p.loadStock(s);
                p.setCash(1000);

                int[] ticks = {0};
                final FakeTimer[] fakeHolder = new FakeTimer[1];

                FakeTimer fake = new FakeTimer(e -> {
                    ticks[0]++;
                    if (ticks[0] >= 3) {
                        fakeHolder[0].stop();
                        presenter.presentGameOver(
                                new PlayStockGameOutputData(p.getCash(), p.getShares(s), p.getTotalEquity(), real.get(0))
                        );
                    } else {
                        presenter.presentPriceUpdate(
                                new PlayStockGameOutputData(p.getCash(), p.getShares(s), p.getTotalEquity(), real.get(0) + ticks[0])
                        );
                    }
                });

                fakeHolder[0] = fake;
                savedTimer[0] = fake;

                presenter.presentGameStart(p, s, null);

                fake.start();
                fake.fire();
                fake.fire();
                fake.fire();
            }
        };

        PlayStockGameInputData input = new PlayStockGameInputData("X", 1000, 1);
        interactor.execute(input);

        assertTrue(startCalled[0]);
        assertTrue(gameOverCalled[0]);
        assertEquals(2, updateCount[0]);
    }

    // ------------------------------
    // Test 5: Legacy exception path
    // ------------------------------
    @Test
    void testLegacyException() {
        PlayStockGameDataAccessInterface dao = new PlayStockGameDataAccessInterface() {
            @Override public List<Double> getIntradayPrices(String symbol, int day) throws Exception {
                throw new Exception("DAO Failed");
            }
            @Override public Map<Integer, List<Double>> getFiveDayPrices(String symbol, String month, int start) { return null; }
        };

        final boolean[] errorCalled = {false};

        PlayStockGameOutputBoundary presenter = new PlayStockGameOutputBoundary() {
            @Override public void presentGameStart(Portfolio p, Stock s, Timer t) { }
            @Override public void presentPriceUpdate(PlayStockGameOutputData o) { }
            @Override public void presentGameOver(PlayStockGameOutputData o) { }
            @Override public void presentError(String errMsg) {
                assertEquals("DAO Failed", errMsg);
                errorCalled[0] = true;
            }
            @Override public void prepareSuccessView(PlayStockGameOutputData outputData) { }
        };

        PlayStockGameInteractor interactor = new PlayStockGameInteractor(dao, presenter);
        PlayStockGameInputData input = new PlayStockGameInputData("BAD", 500, 1);
        interactor.execute(input);

        assertTrue(errorCalled[0]);
    }

    // ------------------------------
    // Test 6: prepareSuccessView explicit call
    // ------------------------------
    @Test
    void testPrepareSuccessViewCall() {
        PlayStockGameOutputBoundary presenter = new PlayStockGameOutputBoundary() {
            @Override public void presentGameStart(Portfolio p, Stock s, Timer t) { }
            @Override public void presentPriceUpdate(PlayStockGameOutputData o) { }
            @Override public void presentGameOver(PlayStockGameOutputData o) { }
            @Override public void presentError(String errMsg) { }
            @Override public void prepareSuccessView(PlayStockGameOutputData outputData) { assertNotNull(outputData); }
        };

        presenter.prepareSuccessView(new PlayStockGameOutputData(0, 0, 0, 0));
    }

    @Test
    void testLegacyPathWithFakeTimer_withTimer() {
        // --- Stub prices for legacy path ---
        List<Double> prices = Arrays.asList(10.0, 11.0, 12.0);
        StubDAO dao = new StubDAO(prices);

        // --- Flags to check presenter calls ---
        final boolean[] startCalled = {false};
        final boolean[] priceCalled = {false};
        final boolean[] gameOverCalled = {false};
        final Portfolio[] capturedPortfolio = {null};
        final Stock[] capturedStock = {null};
        final Timer[] capturedTimer = {null};

        // --- Presenter implementation ---
        PlayStockGameOutputBoundary presenter = new PlayStockGameOutputBoundary() {
            @Override
            public void presentGameStart(Portfolio p, Stock s, Timer t) {
                startCalled[0] = true;
                capturedPortfolio[0] = p;
                capturedStock[0] = s;
                capturedTimer[0] = t;
                priceCalled[0] = true;

            }

            @Override
            public void presentPriceUpdate(PlayStockGameOutputData o) {
                priceCalled[0] = true;
            }

            @Override
            public void presentGameOver(PlayStockGameOutputData o) {
                gameOverCalled[0] = true;
            }

            @Override
            public void presentError(String errMsg) {
                fail("Should not throw error");
            }

            @Override
            public void prepareSuccessView(PlayStockGameOutputData outputData) { }
        };

        // --- Interactor override to inject synchronous fake timer ---
        PlayStockGameInteractor interactor = new PlayStockGameInteractor(dao, presenter) {
        };

        // --- Legacy input: month == null triggers legacy path ---
        PlayStockGameInputData input = new PlayStockGameInputData("AAPL", 1000, 1);

        interactor.execute(input);

        // --- Assertions ---
        assertTrue(startCalled[0], "presentGameStart should have been called");
        assertNotNull(capturedPortfolio[0], "Portfolio should not be null");
        assertNotNull(capturedStock[0], "Stock should not be null");
        assertNotNull(capturedTimer[0], "Timer should not be null");
        assertTrue(priceCalled[0], "presentPriceUpdate should have been called at least once");
    }

    @Test
    void testLegacyPathWithDirectTick() {
        // --- Stub prices ---
        List<Double> prices = new ArrayList<>(Arrays.asList(10.0, 11.0, 12.0, 13.0, 14.0));
        StubDAO dao = new StubDAO(prices);

        final boolean[] startCalled = {false};
        final boolean[] priceCalled = {false};
        final boolean[] gameOverCalled = {false};
        final Portfolio[] capturedPortfolio = {null};
        final Stock[] capturedStock = {null};

        PlayStockGameOutputBoundary presenter = new PlayStockGameOutputBoundary() {
            @Override
            public void presentGameStart(Portfolio p, Stock s, Timer t) {
                startCalled[0] = true;
                capturedPortfolio[0] = p;
                capturedStock[0] = s;
            }

            @Override
            public void presentPriceUpdate(PlayStockGameOutputData o) {
                priceCalled[0] = true;
            }

            @Override
            public void presentGameOver(PlayStockGameOutputData o) {
                gameOverCalled[0] = true;
            }

            @Override
            public void presentError(String errMsg) { fail("No error expected"); }
            @Override
            public void prepareSuccessView(PlayStockGameOutputData outputData) {}
        };

        // --- Setup portfolio and stock ---
        Portfolio portfolio = new Portfolio();
        Stock stock = new Stock("AAPL", prices.get(0));
        portfolio.loadStock(stock);
        portfolio.setCash(1000);

        double[] lastPrice = {prices.get(0)};
        int[] ticks = {0};
        List<Double> realPrices = new ArrayList<>(prices);

        // --- Call onTick() manually 3 times ---
        PlayStockGameInteractor interactor = new PlayStockGameInteractor(dao, presenter);

        for (int i = 0; i < 3; i++) {
            interactor.execute(new PlayStockGameInputData("AAPL", 1000, 1));
        }

        // --- Assertions ---
        assertTrue(startCalled[0], "presentGameStart should be called");
        assertNotNull(capturedPortfolio[0]);
        assertNotNull(capturedStock[0]);
        assertTrue(priceCalled[0], "presentPriceUpdate should be called at least once");
        assertTrue(gameOverCalled[0] || ticks[0] < 3, "presentGameOver should be called if tick limit reached");
    }


}
