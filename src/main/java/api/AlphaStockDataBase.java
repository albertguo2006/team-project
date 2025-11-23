package api;

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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
     * get stock prices from the alphavantage, store it in a json file
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
     * save a json string to the file
     * @param json the string json from the api call
     * @param symbol the stock's symbol (which is also used as the file name)
     */
    @Override
    // save fetched api data to a file with the stock symbol name as the file name
    public void saveToFile(String json, String symbol) {
        String filePath = "src/main/resources/stock_data/" + symbol;
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(json);
        }
        catch (IOException e) { // if there is an issue writing to the file
            System.out.println("Error writing to file: " + filePath);
            e.printStackTrace();
        }
    }

    /**
     * returns the list of open stock prices for the given day
     * @param symbol the stock's symbol
     * @param gameDay day of gameplay (1-5)
     * @return list of stock prices (double)
     * @throws Exception if JSON data doesn't have enough for the given day
     */
    public static List<Double> getIntradayOpensForGameDay(String symbol, int gameDay) throws Exception {
        // only 5 days in the game (for now)
        if (5 < gameDay || gameDay < 1)
            throw new IllegalArgumentException("Invalid day of game: " + gameDay);

        // use mapper to parse json file
        ObjectMapper mapper = new ObjectMapper();
        
        // Load from classpath resources (works in both development and production)
        String resourcePath = "/stock_data/" + symbol;
        java.io.InputStream inputStream = AlphaStockDataBase.class.getResourceAsStream(resourcePath);
        
        if (inputStream == null) {
            // Fallback to file system for development
            String filePath = "src/main/resources/stock_data/" + symbol;
            File file = new File(filePath);
            if (file.exists()) {
                inputStream = new java.io.FileInputStream(file);
            } else {
                throw new RuntimeException("Stock data file not found for symbol: " + symbol);
            }
        }
        
        // Parse JSON from input stream
        JsonNode rootNode = mapper.readTree(inputStream);
        JsonNode timeSeries = rootNode.get("Time Series (5min)");
        
        // If 5min data not found, try daily data
        if (timeSeries == null) {
            timeSeries = rootNode.get("Time Series (Daily)");
        }

        if (timeSeries == null) { // if time series data not found
            throw new RuntimeException("No 'Time Series (5min)' or 'Time Series (Daily)' found.");
        }

        // Check if we have daily or intraday data
        boolean isDailyData = rootNode.get("Time Series (Daily)") != null;
        
        if (isDailyData) {
            // Handle daily data - simulate intraday by creating multiple prices from daily open
            List<String> dates = new ArrayList<>();
            timeSeries.fieldNames().forEachRemaining(dates::add);
            
            // sort dates from most recent to the least recent
            Collections.sort(dates, Collections.reverseOrder());
            
            // Validate we have enough days
            if (dates.size() < 5) {
                throw new RuntimeException("Not enough daily data. Need at least 5 days, found: " + dates.size());
            }
            
            // get data from the right day, where day 1 is the 5th most recent day, day 5 is the most recent past day
            String targetDate = dates.get(5 - gameDay);
            JsonNode dayData = timeSeries.get(targetDate);
            
            // Get the open price for the day
            double openPrice = dayData.get("1. open").asDouble();
            double highPrice = dayData.get("2. high").asDouble();
            double lowPrice = dayData.get("3. low").asDouble();
            double closePrice = dayData.get("4. close").asDouble();
            
            // Simulate intraday data by creating a price progression from open to close
            List<Double> prices = new ArrayList<>();
            int numDataPoints = 78; // Simulate ~78 5-minute intervals in a trading day
            
            for (int i = 0; i < numDataPoints; i++) {
                double progress = (double) i / (numDataPoints - 1);
                // Create a simulated price that moves from open to close with some variation
                double basePrice = openPrice + (closePrice - openPrice) * progress;
                // Add some random variation within the day's range
                double variation = (Math.random() - 0.5) * (highPrice - lowPrice) * 0.3;
                double price = Math.max(lowPrice, Math.min(highPrice, basePrice + variation));
                prices.add(price);
            }
            
            return prices;
        } else {
            // Handle intraday 5min data (original logic)
            List<String> dates = new ArrayList<>();
            timeSeries.fieldNames().forEachRemaining(timestamp -> {
                String date = timestamp.substring(0, 10);
                if (!dates.contains(date)) dates.add(date);
            });

            Collections.sort(dates, Collections.reverseOrder());
            String targetDate = dates.get(5 - gameDay);

            final List<Double> prices = new ArrayList<>();
            final JsonNode finalTimeSeries = timeSeries;
            timeSeries.fieldNames().forEachRemaining(timestamp -> {
                if (timestamp.startsWith(targetDate)) {
                    prices.add(finalTimeSeries.get(timestamp).get("1. open").asDouble());
                }
            });

            Collections.reverse(prices);
            return prices;
        }
    }

/**
    public static void main(String[] args) throws Exception {
        AlphaStockDataBase db = new AlphaStockDataBase("YOUR_API_KEY");

        // db.getStockPrices("VOO");

        System.out.println("Saved prices to VOO.json");

        // gameday 1 = fifth most recent
        List<Double> mostRecent = getIntradayOpensForGameDay("VOO", 1);
        System.out.println("5th most recent prices for VOO: " + mostRecent);

        // gameday 2 = fourth most recent
        List<Double> secondMostRecent = getIntradayOpensForGameDay("VOO", 2);
        System.out.println("4th most recent prices for VOO: " + secondMostRecent);

        // gameday 5 = MOST recent day
        List<Double> fifthMostRecent = getIntradayOpensForGameDay("VOO", 5);
        System.out.println("MOST recent prices for VOO: " + fifthMostRecent);

    }
 **/
}
