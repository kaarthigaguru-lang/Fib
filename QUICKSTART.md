# Quick Start Guide - FIB Filter App

## Getting Started in 5 Minutes

### Step 1: Import Project
1. Open Android Studio
2. Select "Open an Existing Project"
3. Navigate to `FibFilterApp` folder
4. Click "OK" and wait for Gradle sync

### Step 2: Configure API (Optional)
If you want live price updates:

**For Global Stocks (Recommended):**
1. Visit https://www.alphavantage.co/support/#api-key
2. Get free API key (takes 30 seconds)
3. Open `StockRepository.kt`
4. Find line 81: `val result = repository.fetchLivePrice(stock.symbol, apiKey)`
5. Replace `apiKey` with your actual key: `fetchLivePrice(stock.symbol, "YOUR_KEY_HERE")`

**For Indian Stocks:**
- No API key needed
- NSE API works out of the box
- Use symbols like: ACC, TCS, INFY, etc.

### Step 3: Run the App
1. Connect Android device or start emulator
2. Click "Run" (green play button)
3. Wait for build to complete
4. App will install and launch automatically

### Step 4: Add Your First Stock

**Example: Adding AAPL (Apple)**
1. Tap the **+** (plus) button
2. Fill in:
   - Symbol: `AAPL`
   - Name: `Apple Inc.`
   - High: `150.00`
   - Low: `130.00`
   - Current Price: `145.00`
3. Tap "Calculate Fibonacci Levels" button
4. You'll see:
   - 50%: 140.00
   - 61.8%: 142.36
   - 78.6%: 145.72
5. Tap "Save"

**Example: Adding NSE Stock (Indian Market)**
1. Tap **+**
2. Fill in:
   - Symbol: `NSE:TCS` or just `TCS`
   - Name: `Tata Consultancy Services`
   - High: `4000`
   - Low: `3500`
   - Current Price: `3850`
3. Calculate and Save

### Step 5: View Stock Details
1. Tap any stock in the list
2. See:
   - Live chart with Fibonacci levels
   - Current price position
   - All calculated levels

### Step 6: Set Price Alert
1. In stock details, toggle "Enable Alerts"
2. Select target level (e.g., "FIB 618")
3. Tap "Save Alert Settings"
4. App will notify when price approaches that level

## Understanding Fibonacci Levels

The app automatically calculates these key retracement levels:

- **High**: Maximum price in the range
- **78.6%**: Strong resistance/support
- **61.8%**: Golden ratio - most important level
- **50%**: Psychological midpoint
- **Low**: Minimum price in the range

### What the "Level" Badge Means:
- **FIB 618**: Price is within 2% of 61.8% level (GREEN)
- **FIB 78**: Price is near 78.6% level (BLUE)
- **FIB 50**: Price is near 50% level (ORANGE)
- **FALSE**: Price is not near any Fibonacci level (GRAY)

## Tips & Tricks

### Best Practices:
1. **Use Significant High/Low**: Choose meaningful swing highs and lows from chart
2. **Regular Updates**: Refresh prices regularly (use toolbar refresh button)
3. **Set Multiple Alerts**: Track different stocks at different levels
4. **Check Charts**: Visual representation helps understand price action

### Common Symbol Formats:
- **US Stocks**: AAPL, MSFT, GOOGL, TSLA
- **Indian Stocks**: NSE:TCS, NSE:INFY, or just TCS, INFY
- **With Suffix**: AAPL.US, TCS.NS (depends on API)

### Troubleshooting Quick Fixes:

**Can't add stock?**
- Make sure all required fields are filled
- High price must be greater than low price
- Symbol should be in UPPERCASE

**Prices not updating?**
- Check internet connection
- Verify API key is correct
- Try manual refresh (long-press → Refresh Price)

**No notifications?**
- Enable notification permission in phone settings
- Make sure alert is enabled for the stock
- Check battery optimization isn't blocking app

## Sample Stocks to Try

### US Tech Stocks:
- AAPL (Apple)
- MSFT (Microsoft)
- GOOGL (Google)
- AMZN (Amazon)
- TSLA (Tesla)

### Indian Stocks (NSE):
- TCS (Tata Consultancy)
- INFY (Infosys)
- RELIANCE (Reliance Industries)
- HDFCBANK (HDFC Bank)
- WIPRO (Wipro)

## Next Steps

1. **Import Your Watchlist**: Add all stocks you're tracking
2. **Set Strategic Alerts**: Place alerts at key support/resistance levels
3. **Monitor Daily**: Check the app to see which stocks are near Fibonacci levels
4. **Refine Levels**: Update high/low as new swing points form

## Need Help?

- Check the full README.md for detailed documentation
- Look at the app's built-in examples
- Contact support through GitHub issues

Happy Trading! 📈
