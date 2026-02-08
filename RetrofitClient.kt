package com.fibfilter.app.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    
    private const val NSE_BASE_URL = "https://www.nseindia.com/"
    private const val ALPHA_VANTAGE_BASE_URL = "https://www.alphavantage.co/"
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val nseHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("User-Agent", "Mozilla/5.0")
                .header("Accept", "application/json")
                .header("Accept-Language", "en-US,en;q=0.9")
                .build()
            chain.proceed(request)
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val alphaVantageHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val nseRetrofit = Retrofit.Builder()
        .baseUrl(NSE_BASE_URL)
        .client(nseHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    private val alphaVantageRetrofit = Retrofit.Builder()
        .baseUrl(ALPHA_VANTAGE_BASE_URL)
        .client(alphaVantageHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val nseApiService: NseApiService = nseRetrofit.create(NseApiService::class.java)
    val alphaVantageService: AlphaVantageService = alphaVantageRetrofit.create(AlphaVantageService::class.java)
}
