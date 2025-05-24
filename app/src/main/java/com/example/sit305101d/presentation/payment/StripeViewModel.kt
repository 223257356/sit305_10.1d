package com.example.sit305101d.presentation.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sit305101d.domain.payment.CreatePaymentConfigUseCase
import com.example.sit305101d.presentation.payment.StripeUiEvent.OnPaymentPresented
import com.example.sit305101d.presentation.payment.StripeUiEvent.OnResultCallback
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.viewmodel.container

data class StripeUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val paymentSheetResult: PaymentSheetResult? = null
)

sealed interface StripeUiEvent {
    data class CheckOut(
        val amount: Float,
        val currency: String
    ) : StripeUiEvent

    data class OnPaymentPresented(
        val secret: String,
        val configuration: PaymentSheet.Configuration
    ) : StripeUiEvent

    data class OnResultCallback(
        val paymentSheetResult: PaymentSheetResult
    ) : StripeUiEvent
}

@KoinViewModel
class StripeViewModel(
    private val stripeConfigManager: StripeConfigManager,
    private val createPaymentConfigUseCase: CreatePaymentConfigUseCase,
    private val paymentResultEventBus: PaymentResultEventBus
) : ViewModel(), ContainerHost<StripeUiState, StripeUiEvent> {
    override val container: Container<StripeUiState, StripeUiEvent> = container(
        StripeUiState(),
        onCreate = {
            viewModelScope.launch {
                paymentResultEventBus.collect {
                    intent {
                        postSideEffect(OnResultCallback(it))
                    }
                }
            }
        }
    )

    fun onEvent(event: StripeUiEvent) = intent {
        when (event) {
            is StripeUiEvent.CheckOut -> onCheckOut(event)
            else -> {
                /** Do Nothing to Side Effected */
            }
        }
    }

    @OptIn(OrbitExperimental::class)
    private suspend fun onCheckOut(event: StripeUiEvent.CheckOut) = subIntent {
        reduce {
            state.copy(
                isLoading = true,
                isSuccess = false,
                errorMessage = null,
                paymentSheetResult = null
            )
        }
        createPaymentConfigUseCase.execute(
            event.amount,
            event.currency
        ).onSuccess { (paymentConfig, sheetConfig) ->
            stripeConfigManager.initialize(paymentConfig.publishableKey)
            reduce { state.copy(isLoading = false, isSuccess = true) }
            postSideEffect(
                OnPaymentPresented(
                    paymentConfig.paymentIntentClientSecret,
                    sheetConfig
                )
            )
        }.onFailure {
            reduce { state.copy(isLoading = false, isSuccess = false, errorMessage = it.message) }
        }
    }
}
