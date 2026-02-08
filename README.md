# FIB Filter - Stock Fibonacci Retracement Tracker

A comprehensive Android app for tracking stocks with Fibonacci retracement levels, live price updates, and price alerts.

## Features

✅ **Stock Management**
- Add/Edit/Delete stocks with Fibonacci levels
- Auto-calculate Fibonacci retracement levels (50%, 61.8%, 78.6%)
- Track current price against Fibonacci levels

✅ **Live Market Data**
- Connect to live market APIs (NSE India, Alpha Vantage)
- Real-time price updates
- Refresh individual or all stocks

✅ **Price Alerts**
- Set alerts for specific Fibonacci levels
- Background monitoring with WorkManager
- Push notifications when price approaches target levels

✅ **Chart Visualization**
- Interactive charts showing Fibonacci levels
- Visual representation of current price vs levels
- Color-coded level indicators

✅ **Modern UI**
- Material Design 3
- RecyclerView with smooth animations
- Responsive layouts

## Tech Stack

- **Language**: Kotlin
- **Architecture**: MVVM
- **Database**: Room
- **Network**: Retrofit + OkHttp
- **Charts**: MPAndroidChart
- **Background Tasks**: WorkManager
- **UI**: Material Design Components, View Binding

## Setup Instructions

### 1. Prerequisites
- Android Studio Hedgehog or later
- Minimum SDK: 24 (Android 7.0)
- Target SDK: 34 (Android 14)

### 2. Clone and Open Project
```bash
git clone <your-repo-url>
cd FibFilterApp
```
Open the project in Android Studio.

### 3. API Configuration

#### Option A: Alpha Vantage (Recommended for Global Stocks)
1. Get a free API key from https://www.alphavantage.co/support/#api-key
2. Update the API calls in `StockRepository.kt` to include your API key

#### Option B: NSE India (For Indian Stocks)
- No API key required
- Works for NSE-listed stocks
- May have rate limiting

### 4. Build and Run
1. Sync Gradle files
2. Build the project
3. Run on emulator or physical device

## Usage Guide

### Adding a Stock
1. Tap the **+** button
2. Enter stock details:
   - Symbol (e.g., NSE:ACC, AAPL)
   - Company name
   - High price (for Fibonacci calculation)
   - Low price (for Fibonacci calculation)
   - Current price
3. Tap **Calculate Fibonacci Levels** to preview levels
4. Tap **Save**

### Viewing Stock Details
1. Tap any stock in the list
2. View:
   - Current price and level status
   - Interactive Fibonacci chart
   - All Fibonacci levels
   - Alert settings

### Setting Price Alerts
1. Open stock details
2. Toggle **Enable Alerts**
3. Select target Fibonacci level
4. Tap **Save Alert Settings**
5. App will monitor and notify when price approaches target

### Refreshing Prices
- **Single Stock**: Long-press stock → Select "Refresh Price"
- **All Stocks**: Tap refresh icon in toolbar

## Project Structure

```
app/
├── data/
│   ├── local/           # Room database
│   │   ├── AppDatabase.kt
│   │   └── StockDao.kt
│   ├── model/           # Data models
│   │   └── Stock.kt
│   ├── remote/          # API services
│   │   ├── ApiService.kt
│   │   └── RetrofitClient.kt
│   └── repository/      # Repository pattern
│       └── StockRepository.kt
├── ui/
│   ├── activity/        # Activities
│   │   ├── MainActivity.kt
│   │   └── StockDetailActivity.kt
│   ├── adapter/         # RecyclerView adapters
│   │   └── StockAdapter.kt
│   └── viewmodel/       # ViewModels
│       └── StockViewModel.kt
└── worker/              # Background tasks
    └── PriceAlertWorker.kt
```

## Customization

### Change Theme Colors
Edit `res/values/colors.xml` and `res/values/themes.xml`

### Modify Fibonacci Levels
Update calculations in `Stock.kt`:
```kotlin
fib50 = low + (diff * 0.5)
fib618 = low + (diff * 0.618)
fib78 = low + (diff * 0.786)
```

### Add More Alert Levels
Modify the levels list in `StockDetailActivity.kt`:
```kotlin
val levels = listOf("FIB HIGH", "FIB 78", "FIB 618", "FIB 50", "FIB LOW")
```

## API Integration Notes

### NSE India
- Base URL: `https://www.nseindia.com/`
- No authentication required
- Rate limited
- Best for Indian stocks (NSE, BSE)

### Alpha Vantage
- Base URL: `https://www.alphavantage.co/`
- Free tier: 5 API calls/minute, 500/day
- Premium tiers available
- Global stock coverage

### Adding Other APIs
Implement the API service in `ApiService.kt` and update `StockRepository.kt`

## Troubleshooting

### Issue: Prices not updating
- Check internet connection
- Verify API key (if using Alpha Vantage)
- Check symbol format (NSE: prefix for Indian stocks)

### Issue: Notifications not working
- Enable notification permission in Settings
- Check battery optimization settings
- Verify WorkManager is scheduled

### Issue: Charts not displaying
- Ensure MPAndroidChart dependency is properly synced
- Check that Fibonacci levels are calculated

## Future Enhancements

- [ ] Historical price charts
- [ ] Portfolio tracking
- [ ] Multiple timeframe analysis
- [ ] Export/Import stock lists
- [ ] Dark mode
- [ ] Widget for home screen
- [ ] Advanced technical indicators

## License

This project is open source and available under the MIT License.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Support

For issues and questions, please open an issue on GitHub.
