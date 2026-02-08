package com.fibfilter.app.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@Entity(tableName = "stocks")
data class Stock(
    @PrimaryKey
    val symbol: String,
    val name: String,
    val highDate: Long,
    val high: Double,
    val fibHigh: Double?,
    val fibLow: Double?,
    val fib50: Double?,
    val fib618: Double?,
    val fib78: Double?,
    val currentPrice: Double,
    val level: String, // "FALSE", "FIB 618", etc.
    val lastUpdated: Long = System.currentTimeMillis(),
    val alertEnabled: Boolean = false,
    val targetLevel: String? = null // Which fib level to alert on
) : Parcelable {
    
    fun getFibLevels(): Map<String, Double?> {
        return mapOf(
            "FIB HIGH" to fibHigh,
            "FIB LOW" to fibLow,
            "FIB 50" to fib50,
            "FIB 618" to fib618,
            "FIB 78" to fib78
        )
    }
    
    fun isNearLevel(levelName: String, threshold: Double = 2.0): Boolean {
        val levelValue = when(levelName) {
            "FIB HIGH" -> fibHigh
            "FIB LOW" -> fibLow
            "FIB 50" -> fib50
            "FIB 618" -> fib618
            "FIB 78" -> fib78
            else -> null
        } ?: return false
        
        val percentDiff = kotlin.math.abs((currentPrice - levelValue) / levelValue * 100)
        return percentDiff <= threshold
    }
    
    fun calculateFibLevels(high: Double, low: Double): Stock {
        val diff = high - low
        return this.copy(
            fibHigh = high,
            fibLow = low,
            fib50 = low + (diff * 0.5),
            fib618 = low + (diff * 0.618),
            fib78 = low + (diff * 0.786)
        )
    }
}
