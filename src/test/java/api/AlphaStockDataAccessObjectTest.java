package api;

import org.junit.jupiter.api.*;
import use_case.stock_game.play_stock_game.PlayStockGameDataAccessInterface;

import java.nio.file.*;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AlphaStockDataAccessObjectTest {

    private static final Path TEST_RESOURCES = Paths.get("src/main/resources/stock_data");

    private void writeGameDayJson(String symbol, String json) throws Exception {
        Path file = TEST_RESOURCES.resolve(symbol); // no .json suffix
        Files.writeString(file, json);
    }


    @BeforeAll
    static void setupFolder() throws Exception {
        Files.createDirectories(TEST_RESOURCES);
    }

    // Helper to write a JSON file
    private void writeJson(String symbol, String json) throws Exception {
        Path file = TEST_RESOURCES.resolve(symbol + ".json");
        Files.writeString(file, json);
    }

    @Test
    void testGetIntradayPricesSuccess() throws Exception {
        // Arrange: create a JSON file with daily stock data
        String json = """
        {
          "Time Series (Daily)": {
            "2024-02-05": { "1. open": "11", "2. high": "20", "3. low": "5", "4. close": "15" },
            "2024-02-04": { "1. open": "14", "2. high": "20", "3. low": "5", "4. close": "15" },
            "2024-02-03": { "1. open": "17", "2. high": "20", "3. low": "5", "4. close": "15" },
            "2024-02-02": { "1. open": "18", "2. high": "20", "3. low": "5", "4. close": "15" },
            "2024-02-01": { "1. open": "9", "2. high": "20", "3. low": "5", "4. close": "15" }
          }
        }
        """;


        writeGameDayJson("TESTSTOCK", json);

        PlayStockGameDataAccessInterface dao = new AlphaStockDataAccessObject();
        List<Double> prices = dao.getIntradayPrices("TESTSTOCK", 1);

        assertNotNull(prices);
        assertEquals(78, prices.size());
    }

    @Test
    void testGetIntradayPricesInvalidDayThrows() throws Exception {
        String json = """
        {
          "Time Series (Daily)": {
            "2024-02-01": { "1. open": "10", "2. high": "20", "3. low": "5", "4. close": "15" }
          }
        }
        """;

        writeJson("TESTSTOCK2", json);

        PlayStockGameDataAccessInterface dao = new AlphaStockDataAccessObject();

        Exception ex = assertThrows(RuntimeException.class,
                () -> dao.getIntradayPrices("TESTSTOCK2", 6)); // day index too large
        assertTrue(ex.getMessage().contains("Invalid day"));
    }

    @Test
    void testGetIntradayPricesFileMissingThrows() {
        PlayStockGameDataAccessInterface dao = new AlphaStockDataAccessObject();

        Exception ex = assertThrows(RuntimeException.class,
                () -> dao.getIntradayPrices("NOSUCHFILE", 1));
        assertTrue(ex.getMessage().contains("Stock data file not found"));
    }

    @Test
    void testGetFiveDayPrices() throws Exception {
        String json = """
    {
      "Time Series (Daily)": {
        "2024-02-05": { "1. open": "10", "2. high": "20", "3. low": "5", "4. close": "15" },
        "2024-02-04": { "1. open": "11", "2. high": "21", "3. low": "6", "4. close": "16" },
        "2024-02-03": { "1. open": "12", "2. high": "22", "3. low": "7", "4. close": "17" },
        "2024-02-02": { "1. open": "13", "2. high": "23", "3. low": "8", "4. close": "18" },
        "2024-02-01": { "1. open": "14", "2. high": "24", "3. low": "9", "4. close": "19" }
      }
    }
    """;

        String symbol = "FIVEDAYS";
        String month = "2024-02";
        writeJson(symbol + "_" + month, json);  // must include month in filename

        PlayStockGameDataAccessInterface dao = new AlphaStockDataAccessObject();
        Map<Integer, List<Double>> map = dao.getFiveDayPrices(symbol, month, 0);

        assertEquals(5, map.size());
        map.values().forEach(list -> assertEquals(78, list.size()));
    }


    @Test
    void testGetFiveDayPricesInsufficientDaysThrows() throws Exception {
        String json = """
        {
          "Time Series (Daily)": {
            "2024-02-01": { "1. open": "10", "2. high": "20", "3. low": "5", "4. close": "15" }
          }
        }
        """;

        writeJson("TOOFEW", json);

        PlayStockGameDataAccessInterface dao = new AlphaStockDataAccessObject();

        Exception ex = assertThrows(RuntimeException.class,
                () -> dao.getFiveDayPrices("TOOFEW", "2024-02", -1));

        assertTrue(ex.getMessage().contains("Invalid day"));
    }
}
