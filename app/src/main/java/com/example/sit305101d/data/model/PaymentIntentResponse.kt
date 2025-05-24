package com.example.sit305101d.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PaymentIntentResponse(
    val publishableKey: String,
    val paymentIntent: String,
    val customer: String,
    val ephemeralKey: String
)
