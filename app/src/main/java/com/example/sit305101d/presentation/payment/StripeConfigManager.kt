package com.example.sit305101d.presentation.payment

import android.content.Context
import com.stripe.android.PaymentConfiguration
import org.koin.core.annotation.Single

@Single
class StripeConfigManager(private val context: Context) {

    fun initialize(publishableKey: String) {
        PaymentConfiguration.init(context, publishableKey)
    }
}
