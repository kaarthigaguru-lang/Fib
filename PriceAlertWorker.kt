package com.fibfilter.app.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fibfilter.app.R
import com.fibfilter.app.data.local.AppDatabase
import com.fibfilter.app.data.remote.RetrofitClient
import com.fibfilter.app.data.repository.StockRepository
import com.fibfilter.app.ui.activity.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PriceAlertWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    companion object {
        const val CHANNEL_ID = "fib_filter_alerts"
        const val NOTIFICATION_ID = 1001
    }
    
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val database = AppDatabase.getDatabase(applicationContext)
            val repository = StockRepository(
                database.stockDao(),
                RetrofitClient.nseApiService,
                RetrofitClient.alphaVantageService
            )
            
            // Get stocks with alerts enabled
            val stocksWithAlerts = repository.stocksWithAlerts.value ?: emptyList()
            
            for (stock in stocksWithAlerts) {
                // Fetch latest price
                val priceResult = repository.fetchLivePrice(stock.symbol)
                if (priceResult.isSuccess) {
                    val newPrice = priceResult.getOrNull()!!
                    repository.updatePrice(stock.symbol, newPrice)
                    
                    // Check if price is near target level
                    stock.targetLevel?.let { targetLevel ->
                        val updatedStock = stock.copy(currentPrice = newPrice)
                        if (updatedStock.isNearLevel(targetLevel, threshold = 1.0)) {
                            sendNotification(updatedStock, targetLevel, newPrice)
                        }
                    }
                }
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
    
    private fun sendNotification(stock: com.fibfilter.app.data.model.Stock, level: String, price: Double) {
        createNotificationChannel()
        
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("${stock.symbol} Alert")
            .setContentText("Price ₹${String.format("%.2f", price)} is near $level")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(stock.symbol.hashCode(), notification)
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Price Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications when stock prices reach Fibonacci levels"
            }
            
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
