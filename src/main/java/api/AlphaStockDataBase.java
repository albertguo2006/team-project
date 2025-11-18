package api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    /**
     * Fetches stock prices from the AlphaVantage API, and stores it in a JSON file
     * @param symbol the symbol of the stock
     * @throws Exception if something goes wrong (api call doesn't work)
     */
    @Override
    public void getStockPrices(String symbol) throws Exception {
        // create the url using the parameters needed for the API call
        String url = BASE_URL +
                "?function=TIME_SERIES_INTRADAY" +
                "&interval=" + "5min"+ // may change this depending on caffiene levels etc.
                "&outputsize=" + "full"+
                "&symbol=" + symbol +
                "&apikey=" + apiKey;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url)) // url to go to
                .GET()                 // get method from http
                .build();               // build the request

        // send the request, convert returned json to string, store response in response object
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) { // if the api call not successful...
            throw new RuntimeException("API request failed with code: " + response.statusCode());
        }
        else { // otherwise (api call successful, can save data to a JSON file)
            saveToFile(response.body(), symbol);
        }
    }

    /**
     * Saves a string (JSON) to the file
     * @param json the username to look up
     * @param symbol the stock's symbol
     * @throws IOException if something goes wrong when writing to file
     */
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

    /**
     * returns the list of open stock prices for the given day
     * @param symbol the stock's symbol
     * @param gameDay day of gameplay (1-5)
     * @returns list of stock prices (double)
     * @throws Exception if JSON data doesn't have enough for the given day
     */
    public static List<Double> getIntradayOpensForDayIndex(String symbol, int gameDay) throws Exception {
        // only 5 days in the game (for now)
        if (5 < gameDay || gameDay < 1)
            throw new IllegalArgumentException("Invalid day of game: " + gameDay);

        // use mapper to parse json file
        ObjectMapper mapper = new ObjectMapper();
        // open file and extract time series data
        JsonNode timeSeries = mapper.readTree(new File(symbol)).get("Time Series (5min)");

        if (timeSeries == null) { // if time series data not found
            throw new RuntimeException("No 'Time Series (5min)' found.");
        }

        // make list of dates in time series data
        List<String> dates = new ArrayList<>();
        timeSeries.fieldNames().forEachRemaining(timestamp -> {
            String date = timestamp.substring(0, 10);
            // take first 10 chars which is the year, month, day
            if (!dates.contains(date)) dates.add(date);
            // if date not already included, then add
        });

        // the first day of the game should correspond to the fifth most recent day of data
        String targetDate = dates.get(5-gameDay); // matching gameDay to corresponding irl date

        // add all the open stock values to a list for the given gameDay
        List<Double> openStockPrices = new ArrayList<>();
        timeSeries.fieldNames().forEachRemaining(timestamp -> {
            if (timestamp.startsWith(targetDate)) {
                openStockPrices.add(timeSeries.get(timestamp).get("1. open").asDouble());
            }
        });

        return openStockPrices; // return the list of stock prices for that day
    }



    public static void main(String[] args) throws Exception {
        AlphaStockDataBase db = new AlphaStockDataBase("YOUR_API_KEY");

        // db.getStockPrices("VOO");

        System.out.println("Saved prices to VOO.json");

        // 1 = most recent
        List<Double> mostRecent = getIntradayOpensForDayIndex("VOO", 1);
        System.out.println("Most recent prices for VOO: " + mostRecent);

        // 2 = second most recent
        List<Double> secondMostRecent = getIntradayOpensForDayIndex("VOO", 2);
        System.out.println("Second most recent prices for VOO: " + secondMostRecent);

        // 5 = fifth most recent
        List<Double> fifthMostRecent = getIntradayOpensForDayIndex("VOO", 5);
        System.out.println("Fifth recent prices for VOO: " + fifthMostRecent);

    }
}
