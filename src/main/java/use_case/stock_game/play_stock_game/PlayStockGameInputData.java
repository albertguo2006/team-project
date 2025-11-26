package use_case.stock_game.play_stock_game;

/**
 * input data for START stock game use case
**/

public class PlayStockGameInputData {
    public final String symbol;
    public final double startAmount; // initial investing amount
    public final int days;
    public final String month;  // Month in YYYY-MM format (e.g., "2024-11")
    public final int startDayIndex;  // Starting day index for the 5-day period
    public final String periodId;  // Period identifier for tracking played periods
    public final int timerIntervalMs;  // Timer interval in milliseconds (250 normal, 500 slowed)

    // Legacy constructor for backward compatibility
    @Deprecated
    public PlayStockGameInputData(String symbol, double startAmount, int day) {
        this.symbol = symbol;
        this.startAmount = startAmount;
        this.days = day;
        this.month = null;
        this.startDayIndex = 0;
        this.periodId = null;
        this.timerIntervalMs = 250;  // Default normal speed
    }

    // New constructor with month and period information
    public PlayStockGameInputData(String symbol, double startAmount, String month, int startDayIndex, String periodId) {
        this(symbol, startAmount, month, startDayIndex, periodId, 250);  // Default normal speed
    }

    // Constructor with timer interval for buff effects
    public PlayStockGameInputData(String symbol, double startAmount, String month, int startDayIndex, String periodId, int timerIntervalMs) {
        this.symbol = symbol;
        this.startAmount = startAmount;
        this.days = 5;  // Stock game always spans 5 days
        this.month = month;
        this.startDayIndex = startDayIndex;
        this.periodId = periodId;
        this.timerIntervalMs = timerIntervalMs;
    }
}
