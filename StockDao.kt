package com.fibfilter.app.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fibfilter.app.data.model.Stock

@Dao
interface StockDao {
    
    @Query("SELECT * FROM stocks ORDER BY symbol ASC")
    fun getAllStocks(): LiveData<List<Stock>>
    
    @Query("SELECT * FROM stocks WHERE symbol = :symbol")
    suspend fun getStock(symbol: String): Stock?
    
    @Query("SELECT * FROM stocks WHERE alertEnabled = 1")
    fun getStocksWithAlerts(): LiveData<List<Stock>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStock(stock: Stock)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStocks(stocks: List<Stock>)
    
    @Update
    suspend fun updateStock(stock: Stock)
    
    @Delete
    suspend fun deleteStock(stock: Stock)
    
    @Query("DELETE FROM stocks WHERE symbol = :symbol")
    suspend fun deleteStockBySymbol(symbol: String)
    
    @Query("UPDATE stocks SET currentPrice = :price, lastUpdated = :timestamp WHERE symbol = :symbol")
    suspend fun updatePrice(symbol: String, price: Double, timestamp: Long)
    
    @Query("UPDATE stocks SET alertEnabled = :enabled, targetLevel = :targetLevel WHERE symbol = :symbol")
    suspend fun updateAlert(symbol: String, enabled: Boolean, targetLevel: String?)
    
    @Query("SELECT * FROM stocks WHERE level != 'FALSE' ORDER BY symbol ASC")
    fun getActiveStocks(): LiveData<List<Stock>>
    
    @Query("DELETE FROM stocks")
    suspend fun deleteAllStocks()
}
