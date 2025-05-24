package com.example.sit305101d.domain.payment

import com.example.sit305101d.data.repository.StripeRepository
import com.example.sit305101d.domain.model.PaymentConfig
import com.stripe.android.paymentsheet.PaymentSheet
import org.koin.core.annotation.Single
import kotlin.math.roundToInt

@Single
class CreatePaymentConfigUseCase(
    private val stripeRepository: StripeRepository
) {
    suspend fun execute(
        amount: Float,
        currency: String
    ): Result<Pair<PaymentConfig, PaymentSheet.Configuration>> {
        return runCatching {
            val response = stripeRepository.createPaymentIntent(amount.roundToInt(), currency)
            val paymentConfig = PaymentConfig(
                publishableKey = response.publishableKey,
                paymentIntentClientSecret = response.paymentIntent,
                customerId = response.customer,
                ephemeralKeySecret = response.ephemeralKey,
                merchantName = "Your App Name"
            )
            val customerConfig =
                if (paymentConfig.customerId != null && paymentConfig.ephemeralKeySecret != null) {
                    PaymentSheet.CustomerConfiguration(
                        id = paymentConfig.customerId,
                        ephemeralKeySecret = paymentConfig.ephemeralKeySecret
                    )
                } else {
                    null
                }
            val configuration = PaymentSheet.Configuration(
                merchantDisplayName = paymentConfig.merchantName,
                customer = customerConfig,
                allowsDelayedPaymentMethods = paymentConfig.allowsDelayedPaymentMethods
            )
            paymentConfig to configuration
        }
    }
}
