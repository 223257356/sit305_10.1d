package com.example.sit305101d.presentation.payment

import androidx.compose.runtime.compositionLocalOf
import com.stripe.android.paymentsheet.PaymentSheet

val LocalPaymentSheet = compositionLocalOf<PaymentSheet> { error("Payment sheet not provided") }
