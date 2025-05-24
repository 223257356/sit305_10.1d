package com.example.sit305101d.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:5002/" // Updated port to match server

    // Configure logging interceptor for debugging (Optional)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Log request and response bodies
    }

    // Configure OkHttpClient with timeout and logging
    private val okHttpClient = OkHttpClient.Builder()
        .readTimeout(10, TimeUnit.MINUTES) // 10-minute read timeout as per README
        .connectTimeout(30, TimeUnit.SECONDS) // Reasonable connect timeout
        .addInterceptor(loggingInterceptor) // Add logging (remove for production)
        .build()

    // Configure Kotlinx Serialization
    private val json = Json {
        ignoreUnknownKeys = true // Ignore keys not defined in DTOs
        isLenient = true // Allow some malformed JSON if necessary
    }

    val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    // Build Retrofit instance
    val instance: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
