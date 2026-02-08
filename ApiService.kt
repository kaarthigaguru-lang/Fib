package com.fibfilter.app.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// NSE India API Service
interface NseApiService {
    
    @GET("api/quote-equity")
    suspend fun getStockQuote(@Query("symbol") symbol: String): Response<NseQuoteResponse>
    
    @GET("api/equity-stockIndices")
    suspend fun getMarketData(@Query("index") index: String = "NIFTY 50"): Response<NseMarketResponse>
}

// Response data classes
data class NseQuoteResponse(
    val info: StockInfo?,
    val metadata: MetaData?,
    val priceInfo: PriceInfo?
)

data class StockInfo(
    val symbol: String,
    val companyName: String,
    val industry: String?
)

data class MetaData(
    val symbol: String,
    val series: String,
    val lastPrice: Double,
    val change: Double,
    val pChange: Double
)

data class PriceInfo(
    val lastPrice: Double,
    val change: Double,
    val pChange: Double,
    val previousClose: Double,
    val open: Double,
    val close: Double,
    val vwap: Double,
    val lowerCP: String,
    val upperCP: String,
    val pPriceBand: String,
    val basePrice: Double,
    val intraDayHighLow: IntraDayHighLow?,
    val weekHighLow: WeekHighLow?
)

data class IntraDayHighLow(
    val min: Double,
    val max: Double,
    val value: Double
)

data class WeekHighLow(
    val min: Double,
    val minDate: String,
    val max: Double,
    val maxDate: String
)

data class NseMarketResponse(
    val data: List<StockData>
)

data class StockData(
    val symbol: String,
    val lastPrice: Double,
    val change: Double,
    val pChange: Double,
    val open: Double,
    val dayHigh: Double,
    val dayLow: Double,
    val previousClose: Double,
    val yearHigh: Double,
    val yearLow: Double
)

// Alternative API - Alpha Vantage (Free tier available)
interface AlphaVantageService {
    
    @GET("query")
    suspend fun getQuote(
        @Query("function") function: String = "GLOBAL_QUOTE",
        @Query("symbol") symbol: String,
        @Query("apikey") apiKey: String
    ): Response<AlphaVantageQuoteResponse>
    
    @GET("query")
    suspend fun getTimeSeries(
        @Query("function") function: String = "TIME_SERIES_DAILY",
        @Query("symbol") symbol: String,
        @Query("apikey") apiKey: String
    ): Response<AlphaVantageTimeSeriesResponse>
}

data class AlphaVantageQuoteResponse(
    val `Global Quote`: GlobalQuote?
)

data class GlobalQuote(
    val `01. symbol`: String,
    val `02. open`: String,
    val `03. high`: String,
    val `04. low`: String,
    val `05. price`: String,
    val `06. volume`: String,
    val `07. latest trading day`: String,
    val `08. previous close`: String,
    val `09. change`: String,
    val `10. change percent`: String
)

data class AlphaVantageTimeSeriesResponse(
    val `Meta Data`: Map<String, String>?,
    val `Time Series (Daily)`: Map<String, DailyData>?
)

data class DailyData(
    val `1. open`: String,
    val `2. high`: String,
    val `3. low`: String,
    val `4. close`: String,
    val `5. volume`: String
)
