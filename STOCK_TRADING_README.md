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
- Files are named: `{SYMBOL}_{YYYY-MM}.json` (e.g., `TSLA_2024-11.json`)
- Metadata tracking is stored in `src/main/resources/stock_metadata.json`

### Game Flow
1. **Stock Selection**: Player chooses a stock from the dialog showing risk levels
2. **Data Check**: System checks if unplayed data exists; fetches new month if needed
3. **Period Selection**: Random 5-day period is selected from available data
4. **Investment**: Player enters investment amount
5. **Trading**: 5-day trading period begins with real historical prices
6. **Tracking**: After game ends, the period is marked as played

### API Call Optimization
- Fetches entire month of data per API call (maximum data efficiency)
- Only fetches when no unplayed data is available
- Caches all data locally to minimize API calls
- With 15 stocks × 1 month each = 15 API calls initially (well under 25/day limit)

### Played Period Tracking
- Each 5-day period is identified by: `{YYYY-MM}_day{start}-{end}`
- Example: `2024-11_day0-4` represents days 0-4 of November 2024
- Globally tracked (all players share the same pool)
- Prevents replaying the same historical periods

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
   - If no data exists: fetches October 2024 data from Alpha Vantage
   - If all data is played: fetches September 2024 data
5. System selects random unplayed 5-day period (e.g., days 7-11)
6. Player enters investment amount (e.g., $1000)
7. Game starts with real historical prices from those 5 days
8. After game ends, period `2024-10_day7-11` is marked as played

## Troubleshooting

### "No unplayed data available"
- All downloaded periods for that stock have been played
- System will attempt to fetch a new month
- If API limit reached, wait until next day or upgrade to premium tier

### "Failed to fetch stock data"
- Check API key is set correctly
- Verify internet connection
- Check if Alpha Vantage API is operational
- Ensure you haven't exceeded daily API call limit (25 for free tier)

### "Stock data file not found"
- Data needs to be fetched from API first
- System will automatically fetch on first play
- Ensure API key is valid

## Future Enhancements
- Support for per-player tracking (each player has their own history)
- Premium API tier support for unlimited data
- Stock portfolio comparison and statistics
- Achievement system for trading performance
