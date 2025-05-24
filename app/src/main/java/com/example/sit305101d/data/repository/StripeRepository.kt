package com.example.sit305101d.data.repository

import com.example.sit305101d.data.network.RetrofitClient
import com.example.sit305101d.data.network.StripeApiService
import org.koin.core.annotation.Single

interface StripeRepository : StripeApiService

@Single
internal class StripeRepositoryImpl :
    StripeRepository,
    StripeApiService by
    RetrofitClient.retrofit.create(StripeApiService::class.java)
