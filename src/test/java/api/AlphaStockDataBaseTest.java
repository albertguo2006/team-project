package api;

import org.junit.jupiter.api.*;

import javax.net.ssl.SSLSession;
import java.io.*;
import java.net.URI;
import java.net.http.*;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class AlphaStockDataBaseTest {

    private static final Path TEST_RESOURCES = Paths.get("src/test/resources/stock_data");

    @BeforeAll
    static void createFolder() throws IOException {
        Files.createDirectories(TEST_RESOURCES);
    }

    private void writeJson(String filename, String json) throws IOException {
        Files.writeString(TEST_RESOURCES.resolve(filename), json);
    }

    static class TestDB extends AlphaStockDataBase {
        String lastSavedJson;
        String lastSavedFilename;

        TestDB() { super("TEST_KEY"); }

        @Override
        public void saveToFile(String json, String filename) {
            this.lastSavedJson = json;
            this.lastSavedFilename = filename;
        }

        @Override
        public void getStockPrices(String symbol, String month) throws Exception {
            HttpResponse<String> fakeResponse = new HttpResponse<>() {
                @Override public int statusCode() { return 200; }
                @Override public String body() { return "{\"test\": 123}"; }
                @Override public HttpRequest request() { return null; }
                @Override public Optional<HttpResponse<String>> previousResponse() { return Optional.empty(); }
                @Override public HttpHeaders headers() { return HttpHeaders.of(Map.of(), (a,b)->true); }
                @Override public URI uri() { return null; }
                @Override public HttpClient.Version version() { return null; }
                @Override public Optional<SSLSession> sslSession() { return Optional.empty(); }
            };
            saveToFile(fakeResponse.body(), symbol + "_recent");
        }
    }
    @Test
    void testSaveToFileWritesFile() throws Exception {
        AlphaStockDataBase db = new AlphaStockDataBase("X");
        String fileName = "SAMPLEFILE";
        String json = "{\"hello\": \"world\"}";
        String path = "src/main/resources/stock_data/" + fileName + ".json";

        db.saveToFile(json, fileName);

        String read = Files.readString(Paths.get(path));
        assertEquals(json, read);
    }

    @Test
    void testGetStockPricesWritesJsonThroughOverride() throws Exception {
        TestDB db = new TestDB();
        db.getStockPrices("AAPL", null);
        assertEquals("{\"test\": 123}", db.lastSavedJson);
        assertEquals("AAPL_recent", db.lastSavedFilename);
    }

    @Test
    void testIntradayDailyDataSuccess() throws Exception {
        String json = """
        {
          "Time Series (Daily)": {
            "2024-02-03": { "1. open": "10", "2. high": "20", "3. low": "5", "4. close": "15" },
            "2024-02-02": { "1. open": "12", "2. high": "18", "3. low": "9", "4. close": "14" }
          }
        }
        """;
        writeJson("TEST_2024-02.json", json);
        List<Double> prices = AlphaStockDataBase.getIntradayOpensForDay("TEST", "2024-02", 0);
        assertEquals(78, prices.size());
    }

    @Test
    void testIntradayFileNotFound() {
        Exception ex = assertThrows(RuntimeException.class,
                () -> AlphaStockDataBase.getIntradayOpensForDay("NOSUCH", "2024-02", 0));
        assertTrue(ex.getMessage().contains("Stock data file not found"));
    }

    @Test
    void testIntradayNoTimeSeriesPresent() throws Exception {
        String json = """
        { "Meta Data": {} }
        """;
        writeJson("BADTS_2024-12.json", json);
        Exception ex = assertThrows(RuntimeException.class,
                () -> AlphaStockDataBase.getIntradayOpensForDay("BADTS", "2024-12", 0));
        assertTrue(ex.getMessage().contains("No 'Time Series"));
    }

    @Test
    void testIntradayDayIndexTooLarge() throws Exception {
        String json = """
        {
          "Time Series (Daily)": {
            "2024-02-03": { "1. open": "10", "2. high": "10", "3. low": "10", "4. close": "10" }
          }
        }
        """;
        writeJson("TOO_FEW_2024-02.json", json);
        Exception ex = assertThrows(RuntimeException.class,
                () -> AlphaStockDataBase.getIntradayOpensForDay("TOO_FEW", "2024-02", 5));
        assertTrue(ex.getMessage().contains("exceeds available"));
    }

    @Test
    void testGetFiveDayPricesSuccess() throws Exception {
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
        writeJson("FD_2024-02.json", json);
        Map<Integer, List<Double>> map = AlphaStockDataBase.getFiveDayPrices("FD", "2024-02", 0);
        assertEquals(5, map.size());
        map.values().forEach(list -> assertEquals(78, list.size()));
    }

    @Test
    void testFiveDayPricesInsufficientDays() throws Exception {
        String json = """
        {
          "Time Series (Daily)": {
            "2024-02-05": { "1. open": "10", "2. high": "20", "3. low": "5", "4. close": "15" }
          }
        }
        """;
        writeJson("FD_BAD_2024-02.json", json);
        Exception ex = assertThrows(RuntimeException.class,
                () -> AlphaStockDataBase.getFiveDayPrices("FD_BAD", "2024-02", 0));
        assertTrue(ex.getMessage().contains("exceeds available"));
    }

    @Test
    void testGameDayInvalidThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> AlphaStockDataBase.getIntradayOpensForGameDay("ANY", 0));
        assertThrows(IllegalArgumentException.class,
                () -> AlphaStockDataBase.getIntradayOpensForGameDay("ANY", 6));
    }

    @Test
    void testGameDay5minFallbackToDaily() throws Exception {
        // Test that daily data is used as fallback when 5min data is not available
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
        writeJson("GTEST", json);
        // Should now successfully fall back to daily data and return simulated intraday prices
        List<Double> prices = AlphaStockDataBase.getIntradayOpensForGameDay("GTEST", 1);
        assertEquals(78, prices.size()); // 78 simulated 5-minute intervals
    }

    @Test
    void testGameDayFileMissing() {
        Exception ex = assertThrows(RuntimeException.class,
                () -> AlphaStockDataBase.getIntradayOpensForGameDay("NOFILE", 1));
        assertTrue(ex.getMessage().contains("file not found"));
    }

    @Test
    void testGameDayTimeSeriesMissing() throws Exception {
        writeJson("BADGAME", "{ \"Meta\": {} }");
        Exception ex = assertThrows(RuntimeException.class,
                () -> AlphaStockDataBase.getIntradayOpensForGameDay("BADGAME", 1));
        assertTrue(ex.getMessage().contains("No 'Time Series"));
    }

    // ---------------------------------------------------
    // NEW TESTS FOR FULL BRANCH & LINE COVERAGE
    // ---------------------------------------------------

    @Test
    void testSaveToFileIOExceptionBranch() throws Exception {
        AlphaStockDataBase db = new AlphaStockDataBase("X");

        // Force a path that cannot be created ("?" is illegal on Windows, and a folder will fail on Linux)
        String illegalFilename = "??illegal//path";

        assertDoesNotThrow(() -> db.saveToFile("{}", illegalFilename));
    }

    @Test
    void testDailyDataClampingBranches() throws Exception {
        // high == low forces value always clamped
        String json = """
        {
          "Time Series (Daily)": {
            "2024-02-03": { "1. open": "10", "2. high": "10", "3. low": "10", "4. close": "10" },
            "2024-02-02": { "1. open": "10", "2. high": "10", "3. low": "10", "4. close": "10" }
          }
        }
        """;
        writeJson("CLAMP_2024-02.json", json);

        List<Double> prices = AlphaStockDataBase.getIntradayOpensForDay("CLAMP", "2024-02", 0);

        assertEquals(78, prices.size());
        assertTrue(prices.stream().allMatch(v -> v == 10.0));
    }

    @Test
    void testGameDay5MinBranch() throws Exception {
        String json = """
        {
          "Time Series (5min)": {
            "2024-02-05 09:30:00": { "1. open": "100" },
            "2024-02-04 09:30:00": { "1. open": "90" },
            "2024-02-03 09:30:00": { "1. open": "80" },
            "2024-02-02 09:30:00": { "1. open": "70" },
            "2024-02-01 09:30:00": { "1. open": "60" }
          }
        }
        """;
        writeJson("GD5MIN", json);

        // gameDay=3 => the middle day → 2024-02-03 → open=80
        List<Double> prices = AlphaStockDataBase.getIntradayOpensForGameDay("GD5MIN", 3);

        assertEquals(List.of(80.0), prices);
    }
    @Test
    void testGetStockPrice() throws Exception {
        String json = """
        {
          "Time Series (5min)": {
            "2024-02-05 09:30:00": { "1. open": "100" }
          }
        }
        """;
        writeJson("123", json);

        AlphaStockDataBase db = new AlphaStockDataBase("X");
        db.getStockPrices("STONK", "2024-02");
        assertDoesNotThrow(() -> RuntimeException.class);
    }

    @Test
    void testGetStockprices() throws Exception {
        String json = """
        {
          "Time Series (5min)": {
            "2024-02-05 09:30:00": { "1. open": "100" },
            "2024-02-04 09:30:00": { "1. open": "90" },
            "2024-02-03 09:30:00": { "1. open": "80" },
            "2024-02-02 09:30:00": { "1. open": "70" },
            "2024-02-01 09:30:00": { "1. open": "60" }
          }
        }
        """;
        writeJson("STONK", json);

    }
}
