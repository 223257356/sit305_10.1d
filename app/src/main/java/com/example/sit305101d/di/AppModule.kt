package com.example.sit305101d.di

import kotlinx.serialization.json.Json
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan(
    "com.example.sit305101d.data",
    "com.example.sit305101d.domain",
    "com.example.sit305101d.presentation",
)
object AppModule {
    @Single
    fun provideJsonParser(): Json = Json {
        isLenient = true
        ignoreUnknownKeys = true
    }
}
