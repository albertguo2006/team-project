package api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import entity.StockInfo;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Manages stock data, metadata, and tracking of played periods.
 */
public class StockDataManager {
    private static final String METADATA_FILE = "src/main/resources/stock_metadata.json";
    private static final String STOCK_DATA_DIR = "src/main/resources/stock_data/";
    private final ObjectMapper mapper;
    private final AlphaStockDataBase stockDataBase;
    private Map<String, StockInfo> stockMetadata;

    public StockDataManager(String apiKey) {
        this.mapper = new ObjectMapper();
        this.stockDataBase = new AlphaStockDataBase(apiKey);
        loadMetadata();
    }

    /**
     * Loads stock metadata from JSON file.
     */
    private void loadMetadata() {
        stockMetadata = new HashMap<>();
        try {
            File metadataFile = new File(METADATA_FILE);
            if (!metadataFile.exists()) {
                System.err.println("Stock metadata file not found: " + METADATA_FILE);
                return;
            }

            JsonNode root = mapper.readTree(metadataFile);
            JsonNode stocksNode = root.get("stocks");

            if (stocksNode != null && stocksNode.isObject()) {
                stocksNode.fields().forEachRemaining(entry -> {
                    String symbol = entry.getKey();
                    JsonNode stockNode = entry.getValue();

                    StockInfo info = new StockInfo();
                    info.setSymbol(symbol);
                    info.setName(stockNode.get("name").asText());
                    info.setRiskLevel(stockNode.get("riskLevel").asText());
                    info.setVolatility(stockNode.get("volatility").asDouble());

                    // Load available months
                    JsonNode monthsNode = stockNode.get("availableMonths");
                    if (monthsNode != null && monthsNode.isArray()) {
                        List<String> months = new ArrayList<>();
                        monthsNode.forEach(node -> months.add(node.asText()));
                        info.setAvailableMonths(months);
                    }

                    // Load played periods
                    JsonNode periodsNode = stockNode.get("playedPeriods");
                    if (periodsNode != null && periodsNode.isArray()) {
                        List<String> periods = new ArrayList<>();
                        periodsNode.forEach(node -> periods.add(node.asText()));
                        info.setPlayedPeriods(periods);
                    }

                    stockMetadata.put(symbol, info);
                });
            }
        } catch (IOException e) {
            System.err.println("Error loading stock metadata: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Saves stock metadata to JSON file.
     */
    public void saveMetadata() {
        try {
            ObjectNode root = mapper.createObjectNode();
            ObjectNode stocksNode = mapper.createObjectNode();

            for (Map.Entry<String, StockInfo> entry : stockMetadata.entrySet()) {
                String symbol = entry.getKey();
                StockInfo info = entry.getValue();

                ObjectNode stockNode = mapper.createObjectNode();
                stockNode.put("name", info.getName());
                stockNode.put("riskLevel", info.getRiskLevel());
                stockNode.put("volatility", info.getVolatility());

                ArrayNode monthsArray = mapper.createArrayNode();
                info.getAvailableMonths().forEach(monthsArray::add);
                stockNode.set("availableMonths", monthsArray);

                ArrayNode periodsArray = mapper.createArrayNode();
                info.getPlayedPeriods().forEach(periodsArray::add);
                stockNode.set("playedPeriods", periodsArray);

                stocksNode.set(symbol, stockNode);
            }

            root.set("stocks", stocksNode);

            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(METADATA_FILE), root);
        } catch (IOException e) {
            System.err.println("Error saving stock metadata: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gets all available stocks.
     */
    public List<StockInfo> getAllStocks() {
        return new ArrayList<>(stockMetadata.values());
    }

    /**
     * Gets stock info by symbol.
     */
    public StockInfo getStockInfo(String symbol) {
        return stockMetadata.get(symbol);
    }

    /**
     * Ensures stock has data available. Fetches new month if needed.
     * @return true if data is available, false otherwise
     */
    public boolean ensureStockData(String symbol) throws Exception {
        StockInfo info = stockMetadata.get(symbol);
        if (info == null) {
            throw new IllegalArgumentException("Unknown stock symbol: " + symbol);
        }

        // Check if we need to fetch new data
        if (info.getAvailableMonths().isEmpty() || !info.hasUnplayedData()) {
            // Fetch data for a recent month
            String monthToFetch = getNextMonthToFetch(info);
            stockDataBase.getStockPrices(symbol, monthToFetch);

            // Add month to available months
            info.addAvailableMonth(monthToFetch);

            // Calculate volatility from the fetched data
            calculateAndUpdateVolatility(symbol, monthToFetch);

            saveMetadata();
            return true;
        }

        return true;
    }

    /**
     * Determines the next month to fetch for a stock.
     */
    private String getNextMonthToFetch(StockInfo info) {
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        // Start with the previous month (most recent complete month)
        LocalDate candidate = now.minusMonths(1);

        // Find a month that hasn't been fetched yet
        for (int i = 0; i < 24; i++) {  // Look back up to 24 months
            String monthStr = candidate.format(formatter);
            if (!info.getAvailableMonths().contains(monthStr)) {
                return monthStr;
            }
            candidate = candidate.minusMonths(1);
        }

        // If all months are fetched, return the most recent one
        return now.minusMonths(1).format(formatter);
    }

    /**
     * Calculates volatility from stock data and updates metadata.
     */
    private void calculateAndUpdateVolatility(String symbol, String month) {
        try {
            String filePath = STOCK_DATA_DIR + symbol + "_" + month + ".json";
            File file = new File(filePath);

            if (!file.exists()) {
                return;
            }

            JsonNode root = mapper.readTree(file);
            JsonNode timeSeries = root.get("Time Series (5min)");

            if (timeSeries == null) {
                timeSeries = root.get("Time Series (Daily)");
            }

            if (timeSeries == null) {
                return;
            }

            // Extract closing prices
            List<Double> closingPrices = new ArrayList<>();
            timeSeries.fields().forEachRemaining(entry -> {
                JsonNode priceData = entry.getValue();
                double closePrice = priceData.get("4. close").asDouble();
                closingPrices.add(closePrice);
            });

            if (closingPrices.size() < 2) {
                return;
            }

            // Calculate daily returns
            List<Double> returns = new ArrayList<>();
            for (int i = 1; i < closingPrices.size(); i++) {
                double returnValue = (closingPrices.get(i) - closingPrices.get(i - 1)) / closingPrices.get(i - 1);
                returns.add(returnValue);
            }

            // Calculate standard deviation (volatility)
            double mean = returns.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            double variance = returns.stream()
                    .mapToDouble(r -> Math.pow(r - mean, 2))
                    .average()
                    .orElse(0.0);
            double volatility = Math.sqrt(variance) * 100;  // Convert to percentage

            // Update stock info
            StockInfo info = stockMetadata.get(symbol);
            if (info != null) {
                info.setVolatility(volatility);

                // Optionally update risk level based on calculated volatility
                if (volatility < 15) {
                    info.setRiskLevel("LOW");
                } else if (volatility < 30) {
                    info.setRiskLevel("MEDIUM");
                } else {
                    info.setRiskLevel("HIGH");
                }
            }

        } catch (IOException e) {
            System.err.println("Error calculating volatility: " + e.getMessage());
        }
    }

    /**
     * Selects a random unplayed 5-day period for the given stock.
     * @return a map containing the month and start day index, or null if no unplayed data
     */
    public Map<String, Object> selectRandomUnplayedPeriod(String symbol) throws Exception {
        StockInfo info = stockMetadata.get(symbol);
        if (info == null || info.getAvailableMonths().isEmpty()) {
            return null;
        }

        Random random = new Random();
        List<String> months = info.getAvailableMonths();

        // Try up to 50 times to find an unplayed period
        for (int attempt = 0; attempt < 50; attempt++) {
            String month = months.get(random.nextInt(months.size()));

            // Get the number of available days in this month's data
            int availableDays = getAvailableDaysInMonth(symbol, month);

            if (availableDays < 5) {
                continue;  // Not enough data in this month
            }

            // Pick a random start day (0-indexed)
            int maxStartDay = availableDays - 5;
            int startDay = random.nextInt(maxStartDay + 1);

            // Create period identifier
            String periodId = month + "_day" + startDay + "-" + (startDay + 4);

            // Check if this period has been played
            if (!info.getPlayedPeriods().contains(periodId)) {
                Map<String, Object> result = new HashMap<>();
                result.put("month", month);
                result.put("startDay", startDay);
                result.put("periodId", periodId);
                return result;
            }
        }

        return null;  // No unplayed period found
    }

    /**
     * Gets the number of trading days available in a month's data file.
     */
    private int getAvailableDaysInMonth(String symbol, String month) throws Exception {
        String filePath = STOCK_DATA_DIR + symbol + "_" + month + ".json";
        File file = new File(filePath);

        if (!file.exists()) {
            return 0;
        }

        JsonNode root = mapper.readTree(file);
        JsonNode timeSeries = root.get("Time Series (5min)");

        if (timeSeries == null) {
            timeSeries = root.get("Time Series (Daily)");
        }

        if (timeSeries == null) {
            return 0;
        }

        // Count unique dates
        Set<String> uniqueDates = new HashSet<>();
        timeSeries.fieldNames().forEachRemaining(timestamp -> {
            String date = timestamp.substring(0, 10);  // Extract YYYY-MM-DD
            uniqueDates.add(date);
        });

        return uniqueDates.size();
    }

    /**
     * Marks a period as played.
     */
    public void markPeriodAsPlayed(String symbol, String periodId) {
        StockInfo info = stockMetadata.get(symbol);
        if (info != null) {
            info.addPlayedPeriod(periodId);
            saveMetadata();
        }
    }
}
