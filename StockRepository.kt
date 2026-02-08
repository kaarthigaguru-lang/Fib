package com.fibfilter.app.data.repository

import androidx.lifecycle.LiveData
import com.fibfilter.app.data.local.StockDao
import com.fibfilter.app.data.model.Stock
import com.fibfilter.app.data.remote.AlphaVantageService
import com.fibfilter.app.data.remote.NseApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StockRepository(
    private val stockDao: StockDao,
    private val nseApiService: NseApiService,
    private val alphaVantageService: AlphaVantageService
) {
    
    val allStocks: LiveData<List<Stock>> = stockDao.getAllStocks()
    val activeStocks: LiveData<List<Stock>> = stockDao.getActiveStocks()
    val stocksWithAlerts: LiveData<List<Stock>> = stockDao.getStocksWithAlerts()
    
    suspend fun insertStock(stock: Stock) = withContext(Dispatchers.IO) {
        stockDao.insertStock(stock)
    }
    
    suspend fun insertStocks(stocks: List<Stock>) = withContext(Dispatchers.IO) {
        stockDao.insertStocks(stocks)
    }
    
    suspend fun updateStock(stock: Stock) = withContext(Dispatchers.IO) {
        stockDao.updateStock(stock)
    }
    
    suspend fun deleteStock(stock: Stock) = withContext(Dispatchers.IO) {
        stockDao.deleteStock(stock)
    }
    
    suspend fun getStock(symbol: String): Stock? = withContext(Dispatchers.IO) {
        stockDao.getStock(symbol)
    }
    
    suspend fun updatePrice(symbol: String, price: Double) = withContext(Dispatchers.IO) {
        stockDao.updatePrice(symbol, price, System.currentTimeMillis())
    }
    
    suspend fun updateAlert(symbol: String, enabled: Boolean, targetLevel: String?) = withContext(Dispatchers.IO) {
        stockDao.updateAlert(symbol, enabled, targetLevel)
    }
    
    // Fetch live price from API
    suspend fun fetchLivePrice(symbol: String, apiKey: String = ""): Result<Double> = withContext(Dispatchers.IO) {
        try {
            // Try Alpha Vantage (more reliable for global stocks)
            if (apiKey.isNotEmpty()) {
                val response = alphaVantageService.getQuote(symbol = symbol, apiKey = apiKey)
                if (response.isSuccessful) {
                    val price = response.body()?.`Global Quote`?.`05. price`?.toDoubleOrNull()
                    if (price != null) {
                        return@withContext Result.success(price)
                    }
                }
            }
            
            // Fallback to NSE for Indian stocks
            val nseSymbol = symbol.replace("NSE:", "").replace(".NS", "")
            val nseResponse = nseApiService.getStockQuote(nseSymbol)
            if (nseResponse.isSuccessful) {
                val price = nseResponse.body()?.priceInfo?.lastPrice
                if (price != null) {
                    return@withContext Result.success(price)
                }
            }
            
            Result.failure(Exception("Unable to fetch price"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Update all stock prices
    suspend fun refreshAllPrices(apiKey: String = ""): List<Pair<String, Boolean>> = withContext(Dispatchers.IO) {
        val stocks = stockDao.getAllStocks().value ?: emptyList()
        val results = mutableListOf<Pair<String, Boolean>>()
        
        stocks.forEach { stock ->
            val result = fetchLivePrice(stock.symbol, apiKey)
            if (result.isSuccess) {
                val newPrice = result.getOrNull()!!
                updatePrice(stock.symbol, newPrice)
                results.add(stock.symbol to true)
            } else {
                results.add(stock.symbol to false)
            }
        }
        
        results
    }
    
    suspend fun deleteAllStocks() = withContext(Dispatchers.IO) {
        stockDao.deleteAllStocks()
    }
}
