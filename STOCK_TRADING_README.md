# Stock Trading System

## Overview
The stock trading system allows players to select from 15 different stocks with varying risk levels, and trade them during a 5-day in-game period using real historical market data from Alpha Vantage.

## Features
- **15 Stock Options**: Low, medium, and high-risk stocks covering ETFs, blue-chips, and volatile tech stocks
- **Real Market Data**: Historical intraday price data from Alpha Vantage API
- **Smart Data Management**: Automatically fetches and caches stock data by month
- **No Replaying**: Tracks played periods to ensure each session uses unique historical data
- **Risk Calculation**: Volatility is calculated from historical data to classify stocks
- **Random Time Periods**: Each game randomly selects an unplayed 5-day period from available data

## Stock List

### Low Risk (Volatility < 15%)
- VOO - Vanguard S&P 500 ETF
- VTI - Vanguard Total Stock Market ETF
- BND - Vanguard Total Bond Market ETF
- KO - Coca-Cola
- JNJ - Johnson & Johnson

### Medium Risk (15% ≤ Volatility < 30%)
- MSFT - Microsoft
- AAPL - Apple
- DIS - Disney
- WMT - Walmart
- JPM - JPMorgan Chase

### High Risk (Volatility ≥ 30%)
- TSLA - Tesla
- NVDA - NVIDIA
- AMD - AMD
- COIN - Coinbase
- MARA - Marathon Digital

## Setup

### 1. Get an Alpha Vantage API Key
1. Visit [Alpha Vantage](https://www.alphavantage.co/support/#api-key)
2. Sign up for a free API key
3. Free tier allows 25 requests per day (sufficient for initial gameplay)

### 2. Set Environment Variable
Set the `ALPHA_VANTAGE_API_KEY` environment variable:

**Windows:**
```bash
set ALPHA_VANTAGE_API_KEY=your_api_key_here
```

**Linux/Mac:**
```bash
export ALPHA_VANTAGE_API_KEY=your_api_key_here
```

**IntelliJ IDEA:**
1. Run → Edit Configurations
2. Select your run configuration
3. Add environment variable: `ALPHA_VANTAGE_API_KEY=your_api_key_here`

## How It Works

### Data Storage
- Stock data is stored in `src/main/resources/stock_data/` as JSON files
- Files are named: `{SYMBOL}_recent.json` (e.g., `TSLA_recent.json`)
- Metadata tracking is stored in `src/main/resources/stock_metadata.json`

### Game Flow
1. **Stock Selection**: Player chooses a stock from the dialog showing risk levels
2. **Data Check**: System checks if data exists; fetches ~100 days of recent data if needed
3. **Period Selection**: Random 5-day period is selected from available data (unplayed preferred)
4. **Investment**: Player enters investment amount
5. **Trading**: 5-day trading period begins with real historical prices (simulated intraday)
6. **Tracking**: After game ends, the period is marked as played

### Free Tier Optimization
- Uses **TIME_SERIES_DAILY** API (more efficient than intraday for free tier)
- "Compact" output gives ~100 trading days (~5 months of data)
- Each API call provides multiple 5-day periods to play
- Simulates intraday price movement from daily OHLC data
- One API call per stock is typically sufficient for many gameplay sessions

### API Call Optimization
- Fetches ~100 days of data per API call (maximum efficiency)
- Only fetches when no data is available for a stock
- Caches all data locally to minimize API calls
- With 15 stocks = 15 API calls total (well under 25/day free tier limit)
- Data remains valid for extended gameplay

### Played Period Tracking & Replay
- Each 5-day period is identified by: `recent_day{start}-{end}`
- Example: `recent_day15-19` represents days 15-19 from recent data
- Globally tracked (all players share the same pool)
- **Replay enabled**: When all periods are played, system allows replaying
- Replay periods are marked with `_REPLAY` suffix for tracking

## File Structure

```
src/main/
├── java/
│   ├── api/
│   │   ├── AlphaStockDataBase.java       # API communication with Alpha Vantage
│   │   ├── AlphaStockDataAccessObject.java  # Data access implementation
│   │   └── StockDataManager.java         # Manages data fetching and tracking
│   ├── entity/
│   │   ├── Stock.java                    # Stock entity
│   │   └── StockInfo.java                # Stock metadata entity
│   ├── view/
│   │   ├── StockSelectionDialog.java     # Stock selection UI
│   │   └── StockGameView.java            # Trading game UI
│   └── use_case/stock_game/
│       └── play_stock_game/
│           ├── PlayStockGameInteractor.java  # Game logic
│           └── PlayStockGameInputData.java   # Input data structure
└── resources/
    ├── stock_metadata.json               # Tracks available months and played periods
    └── stock_data/                       # Cached stock data JSON files
        ├── VOO_2024-11.json
        ├── TSLA_2024-10.json
        └── ...
```

## Usage Example

1. Player approaches the "Work" location in the game
2. Stock selection dialog appears with all 15 stocks grouped by risk
3. Player selects "TSLA (HIGH RISK)"
4. System checks for available data:
   - If no data exists: fetches ~100 recent trading days from Alpha Vantage
   - Data is cached as `TSLA_recent.json`
5. System selects random 5-day period from the 100 days (preferring unplayed)
   - Example: days 42-46 (unplayed)
6. Player enters investment amount (e.g., $1000)
7. Game starts with simulated intraday prices based on daily OHLC data
8. After game ends, period `recent_day42-46` is marked as played
9. Next time: System will prefer unplayed periods, but can replay if all are played

## Troubleshooting

### "Unable to load data"
- Check that API key is set correctly via `ALPHA_VANTAGE_API_KEY` environment variable
- Verify internet connection
- Check if Alpha Vantage API is operational (status.alphavantage.co)
- Ensure you haven't exceeded daily API call limit (25 for free tier)
- Check console output for detailed error messages

### Data shows wrong dates or insufficient periods
- The system now uses TIME_SERIES_DAILY for better free tier compatibility
- Each stock gets ~100 trading days of data
- This provides 20+ unique 5-day periods per stock
- After all periods are played, replay is automatically enabled

### Replay behavior
- Once all unique 5-day periods are played, the system allows replaying
- Replay periods are marked with `_REPLAY` suffix in metadata
- This ensures the game remains playable indefinitely
- To get fresh data, delete `{SYMBOL}_recent.json` and it will re-fetch

## Future Enhancements
- Support for per-player tracking (each player has their own history)
- Premium API tier support for unlimited data
- Stock portfolio comparison and statistics
- Achievement system for trading performance
