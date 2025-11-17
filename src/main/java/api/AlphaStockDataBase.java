package api;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/*
* stock data base from alpha vantage to fetch stock data from external database
 */

public class AlphaStockDataBase implements StockDataBase {

    private static final String BASE_URL = "https://www.alphavantage.co/query";
    private final String apiKey;
    private final HttpClient httpClient;

    // intialise things
    public AlphaStockDataBase(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newHttpClient();
    }

    // function to
    @Override
    public void getStockPrices(String symbol) throws Exception {
        String url = BASE_URL +
                "?function=TIME_SERIES_DAILY" +
                "&symbol=" + symbol +
                "&apikey=" + apiKey;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("API request failed with code: " + response.statusCode());
        }

        saveToFile(response.body(), symbol);
    }

    // save fetched api data to a file with the stock symbol name as the file name
    private void saveToFile(String json, String symbol) throws IOException {
        try (FileWriter writer = new FileWriter(symbol)) {
            writer.write(json);
        }
        catch  (IOException e) { // if there is an issue writing to the file
            System.out.println("Error writing to file: " + symbol);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        AlphaStockDataBase db = new AlphaStockDataBase("YOUR_API_KEY");

        db.getStockPrices("VOO");

        System.out.println("Saved prices to VOO.json");
    }
}
