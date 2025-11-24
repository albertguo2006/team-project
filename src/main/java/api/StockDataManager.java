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
     * Ensures stock has data available. Fetches data if needed.
     * @return true if data is available, false otherwise
     */
    public boolean ensureStockData(String symbol) throws Exception {
        StockInfo info = stockMetadata.get(symbol);
        if (info == null) {
            throw new IllegalArgumentException("Unknown stock symbol: " + symbol);
        }

        // Check if we need to fetch data (using "recent" as identifier for daily data)
        if (info.getAvailableMonths().isEmpty()) {
            System.out.println("No data available for " + symbol + ", fetching...");

            // Fetch recent data (will get ~100 days)
            stockDataBase.getStockPrices(symbol, null);

            // Add "recent" as available data identifier
            info.addAvailableMonth("recent");

            // Calculate volatility from the fetched data
            calculateAndUpdateVolatility(symbol, "recent");

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
                System.out.println("Fetching month: " + monthStr + " for " + info.getSymbol());
                return monthStr;
            }
            candidate = candidate.minusMonths(1);
        }

        // If all months are fetched, return the most recent one
        String fallback = now.minusMonths(1).format(formatter);
        System.out.println("All months fetched, using fallback: " + fallback);
        return fallback;
    }

    /**
     * Calculates volatility from stock data and updates metadata.
     */
    private void calculateAndUpdateVolatility(String symbol, String dataIdentifier) {
        try {
            String filePath = STOCK_DATA_DIR + symbol + "_" + dataIdentifier + ".json";
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
     * Falls back to replaying data if no unplayed periods exist.
     * @param allowReplay if true, allows replaying previously played periods
     * @return a map containing the data identifier and start day index, or null if no data at all
     */
    public Map<String, Object> selectRandomUnplayedPeriod(String symbol, boolean allowReplay) throws Exception {
        StockInfo info = stockMetadata.get(symbol);
        if (info == null || info.getAvailableMonths().isEmpty()) {
            System.err.println("No available data for " + symbol);
            return null;
        }

        Random random = new Random();
        List<String> dataIdentifiers = info.getAvailableMonths(); // This will be ["recent"] for free tier

        // First, try to find an unplayed period
        for (int attempt = 0; attempt < 50; attempt++) {
            String dataId = dataIdentifiers.get(random.nextInt(dataIdentifiers.size()));

            // Get the number of available days in this data
            int availableDays = getAvailableDaysInData(symbol, dataId);

            System.out.println("Data '" + dataId + "' has " + availableDays + " days for " + symbol);

            if (availableDays < 5) {
                System.out.println("Not enough days in " + dataId + " (need 5, have " + availableDays + ")");
                continue;  // Not enough data
            }

            // Pick a random start day (0-indexed)
            int maxStartDay = availableDays - 5;
            int startDay = random.nextInt(maxStartDay + 1);

            // Create period identifier
            String periodId = dataId + "_day" + startDay + "-" + (startDay + 4);

            // Check if this period has been played
            if (!info.getPlayedPeriods().contains(periodId)) {
                System.out.println("Found unplayed period: " + periodId);
                Map<String, Object> result = new HashMap<>();
                result.put("month", dataId);  // Keep "month" key for backward compatibility
                result.put("startDay", startDay);
                result.put("periodId", periodId);
                return result;
            }
        }

        // If no unplayed period found and replay is allowed, select any valid period
        if (allowReplay) {
            System.out.println("No unplayed periods found, allowing replay for " + symbol);
            for (String dataId : dataIdentifiers) {
                int availableDays = getAvailableDaysInData(symbol, dataId);
                if (availableDays >= 5) {
                    int maxStartDay = availableDays - 5;
                    int startDay = random.nextInt(maxStartDay + 1);
                    String periodId = dataId + "_day" + startDay + "-" + (startDay + 4) + "_REPLAY";

                    System.out.println("Selected replay period: " + periodId);
                    Map<String, Object> result = new HashMap<>();
                    result.put("month", dataId);  // Keep "month" key for backward compatibility
                    result.put("startDay", startDay);
                    result.put("periodId", periodId);
                    return result;
                }
            }
        }

        System.err.println("No valid periods found for " + symbol);
        return null;  // No valid period found at all
    }

    /**
     * Selects a random unplayed 5-day period (with replay fallback enabled by default).
     */
    public Map<String, Object> selectRandomUnplayedPeriod(String symbol) throws Exception {
        return selectRandomUnplayedPeriod(symbol, true);
    }

    /**
     * Gets the number of trading days available in a data file.
     */
    private int getAvailableDaysInData(String symbol, String dataIdentifier) throws Exception {
        String filePath = STOCK_DATA_DIR + symbol + "_" + dataIdentifier + ".json";
        File file = new File(filePath);

        if (!file.exists()) {
            System.err.println("File not found: " + filePath);
            return 0;
        }

        JsonNode root = mapper.readTree(file);
        JsonNode timeSeries = root.get("Time Series (5min)");

        if (timeSeries == null) {
            timeSeries = root.get("Time Series (Daily)");
        }

        if (timeSeries == null) {
            System.err.println("No time series data found in: " + filePath);
            return 0;
        }

        // Count unique dates
        Set<String> uniqueDates = new HashSet<>();
        timeSeries.fieldNames().forEachRemaining(timestamp -> {
            String date = timestamp.length() >= 10 ? timestamp.substring(0, 10) : timestamp;  // Extract YYYY-MM-DD
            uniqueDates.add(date);
        });

        int count = uniqueDates.size();
        System.out.println("Found " + count + " unique trading days in " + filePath);
        return count;
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
