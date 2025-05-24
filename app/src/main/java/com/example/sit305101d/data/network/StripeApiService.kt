package com.example.sit305101d.data.network

import com.example.sit305101d.data.model.PaymentIntentResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface StripeApiService {
    @FormUrlEncoded
    @POST("create-payment-intent")
    suspend fun createPaymentIntent(
        @Field("amount") amount: Int,
        @Field("currency") currency: String
    ): PaymentIntentResponse
}
