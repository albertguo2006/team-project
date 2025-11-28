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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * get stock prices from the alphavantage for a specific month, store it in a json file
     * @param symbol the symbol of the stock
     * @param month the month in YYYY-MM format (e.g., "2024-11"), or null for recent data
     * @throws Exception if something goes wrong (api call doesn't work)
     */
    @Override
    public void getStockPrices(String symbol, String month) throws Exception {
        // For free tier, use TIME_SERIES_DAILY which gives 100 days of data with compact
        // This is much more useful than 100 5-minute data points
        String url = BASE_URL +
                "?function=TIME_SERIES_DAILY" +
                "&outputsize=compact" + // Compact gives 100 most recent days (works for free tier)
                "&symbol=" + symbol +
                "&apikey=" + apiKey;

        // Note: month parameter doesn't work well with free tier, so we just fetch recent data
        // The compact daily data will give us ~100 trading days (about 5 months worth)

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
            // For daily data, we'll use "recent" as the identifier since we can't specify month with free tier
            String filename = symbol + "_recent";
            saveToFile(response.body(), filename);
            System.out.println("Fetched " + symbol + " data and saved to " + filename + ".json");
        }
    }

    /**
     * save a json string to the file
     * @param json the string json from the api call
     * @param filename the filename (without extension) to save to
     */
    @Override
    // save fetched api data to a file with the given filename
    public void saveToFile(String json, String filename) {
        String filePath = "src/main/resources/stock_data/" + filename + ".json";
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(json);
        }
        catch (IOException e) { // if there is an issue writing to the file
            System.out.println("Error writing to file: " + filePath);
            e.printStackTrace();
        }
    }

    /**
     * returns the list of open stock prices for a specific day within a month's data
     * @param symbol the stock's symbol
     * @param month the month in YYYY-MM format (e.g., "2024-11")
     * @param dayIndex the day index (0-based) within the month's trading days
     * @return list of stock prices (double) for that day
     * @throws Exception if JSON data doesn't exist or doesn't have the specified day
     */
    public static List<Double> getIntradayOpensForDay(String symbol, String month, int dayIndex) throws Exception {
        String filename = symbol + "_" + month;

        // use mapper to parse json file
        ObjectMapper mapper = new ObjectMapper();

        // Load from classpath resources (works in both development and production)
        String resourcePath = "/stock_data/" + filename + ".json";
        java.io.InputStream inputStream = AlphaStockDataBase.class.getResourceAsStream(resourcePath);

        if (inputStream == null) {
            // Fallback to file system for development
            String filePath = "src/main/resources/stock_data/" + filename + ".json";
            File file = new File(filePath);
            if (file.exists()) {
                inputStream = new java.io.FileInputStream(file);
            } else {
                throw new RuntimeException("Stock data file not found for symbol: " + symbol + " month: " + month);
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
            if (dayIndex >= dates.size()) {
                throw new RuntimeException("Day index " + dayIndex + " exceeds available data. Only " + dates.size() + " days available.");
            }

            // Get data for the specified day
            String targetDate = dates.get(dayIndex);
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
            // Handle intraday 5min data
            List<String> dates = new ArrayList<>();
            timeSeries.fieldNames().forEachRemaining(timestamp -> {
                String date = timestamp.substring(0, 10);
                if (!dates.contains(date)) dates.add(date);
            });

            Collections.sort(dates, Collections.reverseOrder());

            if (dayIndex >= dates.size()) {
                throw new RuntimeException("Day index " + dayIndex + " exceeds available data. Only " + dates.size() + " days available.");
            }

            String targetDate = dates.get(dayIndex);

            final List<Double> prices = new ArrayList<>();
            final JsonNode finalTimeSeries = timeSeries;
            timeSeries.fieldNames().forEachRemaining(timestamp -> {
                if (timestamp.startsWith(targetDate)) {
                    prices.add(finalTimeSeries.get(timestamp).get("1. open").asDouble());
                }
            });

            Collections.sort(prices);
            return prices;
        }
    }

    /**
     * Returns stock prices for 5 consecutive days starting from the given day index.
     * This is used for the stock game which spans 5 game days.
     * @param symbol the stock's symbol
     * @param month the month in YYYY-MM format
     * @param startDayIndex the starting day index (0-based)
     * @return a map where keys are game day numbers (1-5) and values are lists of prices
     * @throws Exception if data is insufficient
     */
    public static Map<Integer, List<Double>> getFiveDayPrices(String symbol, String month, int startDayIndex) throws Exception {
        Map<Integer, List<Double>> gameDayPrices = new HashMap<>();
        // only 5 days in the game (for now)
        if (4 < startDayIndex || startDayIndex < 0)
            throw new IllegalArgumentException("Invalid day of game: " + startDayIndex);

        for (int i = 0; i < 5; i++) {
            int dayIndex = startDayIndex + i;
            int gameDay = i + 1;  // Game days are 1-indexed

            List<Double> prices = getIntradayOpensForDay(symbol, month, dayIndex);
            gameDayPrices.put(gameDay, prices);
        }

        return gameDayPrices;
    }

    /**
     * returns the list of open stock prices for the given day (legacy method)
     * @param symbol the stock's symbol
     * @param gameDay day of gameplay (1-5)
     * @return list of stock prices (double)
     * @throws Exception if JSON data doesn't have enough for the given day
     */
    @Deprecated
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
}
