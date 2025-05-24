package com.example.sit305101d.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PaymentConfig(
    val publishableKey: String,
    val paymentIntentClientSecret: String,
    val customerId: String? = null,
    val ephemeralKeySecret: String? = null,
    val merchantName: String = "Your Merchant Name",
    val allowsDelayedPaymentMethods: Boolean = true
)
