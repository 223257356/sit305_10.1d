package com.example.sit305101d.presentation.payment

import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.PaymentSheetResultCallback
import org.koin.core.annotation.Single

@Single
class PaymentResultCallback(
    private val paymentResultEventBus: PaymentResultEventBus
) : PaymentSheetResultCallback {

    override fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        paymentResultEventBus.post(paymentSheetResult)
    }
}
