import api.AlphaStockDataBase;
import org.junit.jupiter.api.*;
import java.io.FileWriter;
import java.util.List;

/*
* testing for alpha stock data base,
* specifically the methods that get the data from the api (using mock json file)
* and the extracting method which extracts data from the json for each gameDay
*/

public class AlphaStockDataBaseTest {



    @Test
    public void testGetIntradayOpensForDayIndex() throws Exception {

        String json = """
        {
          "Time Series (5min)": {
            "2025-03-24 10:05:00": { "1. open": "439.7" },
            "2025-03-24 13:35:00": { "1. open": "421.1" },
            "2025-03-21 10:05:00": { "1. open": "439.7" },
            "2025-03-21 13:35:00": { "1. open": "421.1" },
            "2025-03-20 10:05:00": { "1. open": "350.7" },
            "2025-03-20 13:35:00": { "1. open": "390.3" },
            "2025-03-19 16:00:00": { "1. open": "200.2" },
            "2025-03-19 15:55:00": { "1. open": "201.0" },
            "2025-03-18 16:00:00": { "1. open": "100.4" },
            "2025-03-18 15:55:00": { "1. open": "101.0" }
          }
        }
        """;

        try (FileWriter writer = new FileWriter("time_series_test.json")) {
            writer.write(json);
        }

        // STEP 2 — call method
        List<Double> result =
                AlphaStockDataBase.getIntradayOpensForGameDay("time_series_test.json", 1);
        System.out.println(result);
        // STEP 3 — assert results
        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.contains(101.0));
        Assertions.assertTrue(result.contains(100.4));
    }
}
