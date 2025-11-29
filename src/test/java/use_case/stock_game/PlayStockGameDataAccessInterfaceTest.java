package use_case.stock_game;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PlayStockGameDataAccessInterfaceTest {

    /** A simple concrete implementation to exercise the interface methods */
    static class DummyDAO implements PlayStockGameDataAccessInterface {

        @Override
        public List<Double> getIntradayPrices(String symbol, int day) {
            return List.of(1.0, 2.0, 3.0);
        }

        @Override
        public Map<Integer, List<Double>> getFiveDayPrices(String symbol, String month, int startDayIndex) {
            return Map.of(
                    1, List.of(10.0),
                    2, List.of(20.0),
                    3, List.of(30.0),
                    4, List.of(40.0),
                    5, List.of(50.0)
            );
        }
    }

    @Test
    void testInterfaceMethodsExecute() throws Exception {
        PlayStockGameDataAccessInterface dao = new DummyDAO();

        // Call legacy method
        List<Double> intraday = dao.getIntradayPrices("AAPL", 1);
        assertEquals(List.of(1.0, 2.0, 3.0), intraday);

        // Call 5-day method
        Map<Integer, List<Double>> five = dao.getFiveDayPrices("AAPL", "2024-02", 0);
        assertEquals(5, five.size());
        assertEquals(List.of(10.0), five.get(1));
        assertEquals(List.of(50.0), five.get(5));
    }
}
