package com.fibfilter.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fibfilter.app.data.local.AppDatabase
import com.fibfilter.app.data.model.Stock
import com.fibfilter.app.data.remote.RetrofitClient
import com.fibfilter.app.data.repository.StockRepository
import kotlinx.coroutines.launch

class StockViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: StockRepository
    val allStocks: LiveData<List<Stock>>
    val activeStocks: LiveData<List<Stock>>
    val stocksWithAlerts: LiveData<List<Stock>>
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage
    
    private val _successMessage = MutableLiveData<String>()
    val successMessage: LiveData<String> = _successMessage
    
    init {
        val stockDao = AppDatabase.getDatabase(application).stockDao()
        repository = StockRepository(
            stockDao,
            RetrofitClient.nseApiService,
            RetrofitClient.alphaVantageService
        )
        allStocks = repository.allStocks
        activeStocks = repository.activeStocks
        stocksWithAlerts = repository.stocksWithAlerts
    }
    
    fun insertStock(stock: Stock) = viewModelScope.launch {
        try {
            repository.insertStock(stock)
            _successMessage.value = "Stock added successfully"
        } catch (e: Exception) {
            _errorMessage.value = "Error adding stock: ${e.message}"
        }
    }
    
    fun updateStock(stock: Stock) = viewModelScope.launch {
        try {
            repository.updateStock(stock)
            _successMessage.value = "Stock updated successfully"
        } catch (e: Exception) {
            _errorMessage.value = "Error updating stock: ${e.message}"
        }
    }
    
    fun deleteStock(stock: Stock) = viewModelScope.launch {
        try {
            repository.deleteStock(stock)
            _successMessage.value = "Stock deleted successfully"
        } catch (e: Exception) {
            _errorMessage.value = "Error deleting stock: ${e.message}"
        }
    }
    
    fun updateAlert(symbol: String, enabled: Boolean, targetLevel: String?) = viewModelScope.launch {
        try {
            repository.updateAlert(symbol, enabled, targetLevel)
            _successMessage.value = if (enabled) "Alert enabled" else "Alert disabled"
        } catch (e: Exception) {
            _errorMessage.value = "Error updating alert: ${e.message}"
        }
    }
    
    fun refreshPrice(symbol: String, apiKey: String = "") = viewModelScope.launch {
        _isLoading.value = true
        try {
            val result = repository.fetchLivePrice(symbol, apiKey)
            if (result.isSuccess) {
                val price = result.getOrNull()!!
                repository.updatePrice(symbol, price)
                _successMessage.value = "$symbol updated: ₹${String.format("%.2f", price)}"
            } else {
                _errorMessage.value = "Failed to fetch price for $symbol"
            }
        } catch (e: Exception) {
            _errorMessage.value = "Error: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }
    
    fun refreshAllPrices(apiKey: String = "") = viewModelScope.launch {
        _isLoading.value = true
        try {
            val results = repository.refreshAllPrices(apiKey)
            val successCount = results.count { it.second }
            val totalCount = results.size
            _successMessage.value = "Updated $successCount of $totalCount stocks"
        } catch (e: Exception) {
            _errorMessage.value = "Error refreshing prices: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }
    
    fun getStock(symbol: String, callback: (Stock?) -> Unit) = viewModelScope.launch {
        val stock = repository.getStock(symbol)
        callback(stock)
    }
    
    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }
}
